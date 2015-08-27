(ns slack-slurper.slurper
  (:require [aleph.http :as http]
            [clj-slack-client.web :as slack-api]
            [manifold.deferred :as d]
            [clojure.core.async :as async]
            [manifold.stream :as s]
            [clojure.tools.logging :as log]
            [slack-slurper.heartbeat :as hb]))

(def api-token (System/getenv "SLACK_SLURPER_TOKEN"))

(defn get-fresh-ws-url []
  (:url (slack-api/rtm-start api-token)))

(defn ws-connection [url]
  @(http/websocket-client url))

(defn listen [f conn]
  (async/go
    (loop [message @(s/take! conn)]
      (f message)
      (if (not (s/closed? conn))
        (recur @(s/take! conn))
        (log/info "Stream closed. Exiting. Stream: " conn)))))

(defn msg-handler [message]
  (log/info message))

(defn slurp-it []
  (let [url (get-fresh-ws-url)
        connection (ws-connection url)]
    (log/info "Starting slurper loop with url: " url " and connection: " connection)
    (hb/heartbeat conn)
    (listen msg-handler connection)))


;; TODO -- Heartbeat / Pinging
;; get https://github.com/dakrone/cheshire for json
;; send slack messages like:
;; { "id": 1234, "type": "ping"}

