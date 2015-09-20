(ns slack-slurper.connection
  (:require [aleph.http :as http]
            [clj-slack-client.web :as slack-api]))

(def api-token (System/getenv "SLACK_SLURPER_TOKEN"))

(defn get-fresh-ws-url []
  (:url (slack-api/rtm-start api-token)))

(defn ws-stream
  ([] (ws-stream (get-fresh-ws-url)))
  ([url] @(http/websocket-client url)))

(def users (memoize (partial slack-api/users-list api-token)))
(def channels (memoize (partial slack-api/channels-list api-token)))

