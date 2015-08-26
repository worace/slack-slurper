(ns slack-slurper.core
  (:require [clj-slack-client.web :as slack-api]
            [aleph.http :as http]
            [manifold.deferred :as d]
            [clojure.core.async :as async]
            [manifold.stream :as s]
            [clojure.tools.logging :as log]
            [slack-slurper.logging]))

(slack-slurper.logging/configure-logging!)

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
  (log/info "Received message!")
  (log/info message))

(defn hang []
  (while true (Thread/sleep 1000)))

(defn -main [& args]
  (log/info "***** Slack Slurper Starting *****", args)
  (listen msg-handler (ws-connection (get-fresh-ws-url)))
  (hang))


;; (async/go (loop [message @(s/take! conn)] (println message) (recur @(s/take! conn))) )
