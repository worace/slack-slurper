(ns slack-slurper.indexing
  (:require [cheshire.core :as json]
            [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]))


(def es-conn (esr/connect "http://127.0.0.1:9200"))

(defn create-index [conn name]
  (esi/create conn name))

(defn extract-message [string]
  (last (clojure.string/split string #"- ")))

;; expected message types:
#_#{"message"
  "user_typing"
  "im_created"
  "star_removed"
  "user_change"
  "reaction_added"
  "file_change"
  "pong"
  "reaction_removed"
  "star_added"
  "emoji_changed"
  "file_public"
  "presence_change"
  "file_shared"}

;; subtypes -- some messages have special subtypes
;; good - file_share
;; ignore - channel joined

(defn log-files
  ([] (log-files "./logs"))
  ([dir] (filter (fn [f] (.isFile f))
                 (file-seq (clojure.java.io/file dir)))))

#_(->> logs
     (map extract-message)
     (filter (fn [m] (.contains m "{")))
     (map json/parse-string))
