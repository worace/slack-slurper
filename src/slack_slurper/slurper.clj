(ns slack-slurper.slurper
  (:require [aleph.http :as http]
            [manifold.deferred :as d]
            [clojure.core.async :as async]
            [manifold.stream :as s]
            [clojure.tools.logging :as log]))

(def api-token (System/getenv "SLACK_SLURPER_TOKEN"))

(defn get-fresh-ws-url []
  (:url (slack-api/rtm-start api-token)))

(defn ws-connection [url]
  @(http/websocket-client url))

(defn listen [f conn]
  (async/go
    (loop [message @(s/take! conn)]
      (f message)
      (recur @(s/take! conn)))))

(defn msg-handler [message]
  (log/info message))

(defn slurp []
  (listen msg-handler (ws-connection (get-fresh-ws-url))))
