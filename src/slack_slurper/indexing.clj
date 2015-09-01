(ns slack-slurper.indexing
  (:require [cheshire.core :as json]
            [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.query         :as q]
            [clojurewerkz.elastisch.rest.document :as esd]))


(def es-conn (esr/connect "http://127.0.0.1:9200"))
(def index-name "slack_slurper")
(def mappings
  {"messages" {:properties {:user    {:type "string" :store "yes"}
                            :channel {:type "string" :store "yes"}
                            :text    {:type "string" :store "yes" :analyzer "standard"}
                            :subtype {:type "string" :store "yes"}
                            :ts      {:type "string" :store "yes"}}}})

(def create-index! (partial esi/create es-conn index-name :mappings mappings))
(def delete-index! (partial esi/delete es-conn index-name))

(defn log-files
  ([] (log-files "./logs"))
  ([dir] (filter (fn [f] (and (.isFile f)
                              (.endsWith (.getName f) ".log")))
                 (file-seq (clojure.java.io/file dir)))))

(defn extract-message [string]
  (last (clojure.string/split string #"- ")))

(defn log-file->messages [f]
  (with-open [r (clojure.java.io/reader f)]
    (doall (->> (line-seq r)
                (map extract-message)
                (filter (fn [m] (.contains m "{\"type")))
                (map json/parse-string)
                (filter (fn [m] (= "message" (m "type"))))) )))

(defn messages [log-files]
  (mapcat log-file->messages log-files))

(defn message-id [m]
  (str (m "channel") "-" (m "ts")))

(defn index-message! [m]
  (esd/create es-conn
              index-name
              "message"
              (dissoc m "type" "team")
              :id (message-id m)))

(defn index-messages! [messages]
  (doseq [m messages]
    (index-message! m)))

(defn rebuild-index!
  "Destroys and rebuilds the index using existing log files as input source."
  []
  (delete-index!)
  (create-index!)
  (index-messages! (messages (log-files))))
