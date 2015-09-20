(ns slack-slurper.indexing-test
  (:require [clojure.test :refer :all]
            [environ.core :refer [env]]
            [clojurewerkz.elastisch.rest.index :as esi]
            [slack-slurper.indexing :refer :all]))


(deftest test-destroying-index
  (testing "it deletes index in ES"
    (delete-index!)
    (is (not (esi/exists? es-conn index-name)))))

(deftest test-creating-index
  (testing "creates index in es"
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


(deftest test-indexing-log-files
  )
