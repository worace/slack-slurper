(ns slack-slurper.core
  (:require [clj-slack-client.web :as slack-api]
            [aleph.http :as http]
            [manifold.deferred :as d]
            [clojure.core.async :as async]
            [manifold.stream :as s]))


(def api-token "")

(defn get-fresh-ws-url []
  (:url (slack-api/rtm-start api-token)))

(defn ws-connection [url]
  @(http/websocket-client url))

(defn listen [f conn]
  (async/go
    (loop [message @(s/take! conn)]
      (f message)
      (recur @(s/take! conn)))))

(defn -main [& args]
  (println "starting app with args: " args))

;; (async/go (loop [message @(s/take! conn)] (println message) (recur @(s/take! conn))) )
