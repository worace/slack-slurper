(ns slack-slurper.heartbeat
  (:require [manifold.stream :as s]
            [clojure.core.async :as a]
            [clojure.tools.logging :as log]
            [cheshire.core :as json]))

(defn ping-id [] (System/currentTimeMillis))

(defn ping [conn]
  (s/put! conn
          (json/generate-string {:id (ping-id) :type "ping"} )))

(defn heartbeat [conn]
  (a/go
    (while (not (s/closed? conn))
      (log/info "pinging connection: " (ping conn))
      (Thread/sleep 10000))))
