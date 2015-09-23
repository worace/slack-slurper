(ns slack-slurper.indexing
  (:require [cheshire.core :as json]
            [manifold.stream :as s]
            [environ.core :refer [env]]
            [slack-slurper.connection :as slack]
            [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.query         :as q]
            [clojurewerkz.elastisch.rest.document :as esd]))


(def es-conn (esr/connect (env :es-host)))
(def index-name (env :es-index-name))
(def mapping-name "messages")
(def mappings
  {mapping-name {:properties {:user    {:type "string" :store "yes"}
                              :username {:type "string" :store "yes"}
                              :user_real_name {:type "string" :store "yes"}
                              :channel {:type "string" :store "yes"}
                              :text    {:type "string" :store "yes" :analyzer "standard"}
                              :subtype {:type "string" :store "yes"}
                              :ts      {:type "string" :store "yes"}}}})

(def create-index! (partial esi/create es-conn index-name :mappings mappings))
(def delete-index! (partial esi/delete es-conn index-name))
(def query (partial esd/search es-conn index-name mapping-name :query))

#_(esd/search es-conn index-name "messages" :query (q/term :text "horace"))

(defn log-files
  ([] (log-files (env :log-dir)))
  ([dir] (filter (fn [f] (and (.isFile f)
                              (.contains (.getName f) ".log")))
                 (file-seq (clojure.java.io/file dir)))))

(defn extract-message [string]
  (last (clojure.string/split string #"- ")))

(defn message-stream
  ([] (message-stream (s/stream)))
  ([stream] (->> stream
                 (s/map #(last (clojure.string/split % #"- ")))
                 (s/filter #(.contains % "{\"type"))
                 (s/map json/parse-string)
                 (s/filter #(= "message" (% "type"))))))

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

(defn with-user-info [m]
  (let [uid (m "user")
        u ((slack/users) uid)]
    (-> m
        (assoc "username" (u :name))
        (assoc "user_real_name" (u :real_name)))))

(defn prep-message
  "take JSON payload out of slack RTM api and prepare it for indexing"
  [m]
  (-> m
      (dissoc "type" "team")
      (with-user-info)
      ))

;; message keys:
;; type, channel, user, text, ts, team
;; to add: username, user_full_name, user_first_name, user_last_name
(defn index-message! [m]
  (esd/create es-conn
              index-name
              mapping-name
              (prep-message m)
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
