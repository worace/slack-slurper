(ns slack-slurper.indexing-test
  (:require [clojure.test :refer :all]
            [environ.core :refer [env]]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.query         :as q]
            [slack-slurper.connection]
            [slack-slurper.indexing :refer :all]))

(defn retried-q
  ([pattern] (retried-q pattern 0))
  ([pattern attempts]
   (let [res (query pattern)]
     (if (> (get-in res [:hits :total]) 0)
       res
       (if (< attempts 15)
         (do
           (Thread/sleep 200)
           (recur pattern (+ 1 attempts)))
         (do (println "WARNING: Q Timed Out") res))))))

(deftest test-destroying-index
  (testing "it deletes index in ES"
    (delete-index!)
    (is (not (esi/exists? es-conn index-name)))))

(deftest test-creating-index
  (testing "creates index in es"
    (delete-index!)
    (create-index!)
    (is (esi/exists? es-conn index-name))))

(deftest test-log-files
  (testing "finds all logfiles in provided dir"
    (is (= 2 (count (log-files "./test/data"))))))

(deftest test-log-file->messages
  (testing "extracting actual messages from log files"
    (let [f (first (log-files "./test/data"))
          messages (log-file->messages f)
          users (into #{} (map #(get % "user") messages))]
      (is (= 3 (count messages)))
      (is (= #{"U02C40LBY" "U04RWRJ5G" "U04UALVNT"} users))
      )))

(deftest test-all-messages
  (testing "extracts all messages from list of log files"
    (let [messages (messages (log-files))]
      (is (= 6 (count messages)))
      (is (seq? messages)))))

(deftest test-message-id
  (testing "combines channel and ts to make id"
    (is (= "pizza-1234"
           (message-id {"channel" "pizza" "ts" "1234"})))))

(deftest test-indexing-and-querying
  (testing "builds searchable index"
    (rebuild-index!)
    (is (= 6 (get-in (retried-q (q/match-all)) [:hits :total])))
    (is (= 1 (get-in (retried-q (q/term :text "cuz")) [:hits :total])))
    ))

(def sample-message
  {"type" "message",
   "channel" "C056H7MSP"
   "user" "U06U9LUMA"
   "text" "<@U07U5SQFM> Yea do it! Right now it?s sales_engine"
   "ts" "1440734427.000194"
   "team" "T029P2S9M"})

(def sample-users
  (read-string (slurp "./test/data/sample_users.edn")))

(deftest test-prepping-message
  (with-redefs [slack-slurper.connection/users (fn [] sample-users)]
    (testing "adds and removes appropriate fields"
    (let [prepped (prep-message sample-message)]
      (is (nil? (prepped "team")))
      (is (nil? (prepped "type")))
      (is (= "regis" (prepped "username")))
      (is (= "Regis Boudinot" (prepped "user_real_name")))))))

(deftest test-searching-for-users
  (with-redefs [slack-slurper.connection/users (fn [] sample-users)]
    (testing "finds result where user is either mentioned or speaking"
      (rebuild-index!)
      (let [by-uname (retried-q (q/term :username "jmejia"))]
        (is (= 1 (get-in by-uname [:hits :total])))))))

(deftest test-searching-full-name
  (with-redefs [slack-slurper.connection/users (fn [] sample-users)]
    (testing "finds em"
      (rebuild-index!)
      (let [lc (retried-q (q/term :user_real_name "justin"))
            last (retried-q (q/term :user_real_name "holzman"))]
        (is (= 1 (get-in lc [:hits :total])))
        (is (= 1 (get-in last [:hits :total])))))))
