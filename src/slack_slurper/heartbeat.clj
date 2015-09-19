(ns slack-slurper.heartbeat
  (:require [manifold.stream :as s]
            [clojure.core.async :as a]
            [clojure.tools.logging :as log]
            [cheshire.core :as json]))

(defn ping-id [] (System/currentTimeMillis))

(defn message []
  (json/generate-string {:id (ping-id) :type "ping"} ))

(defn heartbeat
  "Takes a stream, a function for producing new HB messages,
   a kill-switch, and a time interval (in ms). As long as kill switch
   is true, will invoke msg function and put resulting value
   onto the stream every <time interval>. If no interval is
   provided, defaults to 10 seconds"
  ([stream msg-generator switch]
   (heartbeat stream msg-generator switch 10000))
  ([stream msg-generator switch interval]
   (a/go
     (while (and @switch (not (s/closed? stream)))
       (s/put! stream (msg-generator))
       (Thread/sleep interval)))))
