(ns slack-slurper.connection
  (:require [aleph.http :as http]
            [clj-slack-client.web :as slack-api]))

(def api-token (System/getenv "SLACK_SLURPER_TOKEN"))

(defn get-fresh-ws-url []
  (:url (slack-api/rtm-start api-token)))

(defn ws-stream
  ([] (ws-stream (get-fresh-ws-url)))
  ([url] @(http/websocket-client url)))

(defn indexed-users []
  (reduce (fn [map user] (assoc map (:id user) user))
          (slack-api/users-list api-token)))

(def users (memoize indexed-users))
(def channels (memoize (partial slack-api/channels-list api-token)))

