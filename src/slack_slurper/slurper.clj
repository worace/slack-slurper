(ns slack-slurper.slurper
  (:require [aleph.http :as http]
            [clj-slack-client.web :as slack-api]
            [manifold.deferred :as d]
            [clojure.core.async :as async]
            [manifold.stream :as s]
            [clojure.tools.logging :as log]
            [slack-slurper.heartbeat :as hb]))


#_(defn message-stream
  ([] (message-stream (s/stream)))
  ([stream] (->> stream
                 (s/map #(last (clojure.string/split % #"- ")))
                 (s/filter #(.contains % "{\"type"))
                 (s/map json/parse-string)
                 (s/filter #(= "message" (% "type"))))))


#_(defn log-file->messages [f]
  (with-open [r (clojure.java.io/reader f)]
    (doall (->> (line-seq r)
                (map extract-message)
                (filter (fn [m] (.contains m "{\"type")))
                (map json/parse-string)
                (filter (fn [m] (= "message" (m "type"))))) )))
