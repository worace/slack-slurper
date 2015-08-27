(ns slack-slurper.logging
  (:require [clojure.tools.logging :as log]
            [clj-logging-config.log4j :as conf]))

(def layout (org.apache.log4j.PatternLayout. "%d{ISO8601} %-5p %c - %m%n"))

(defn configure-logging! []
  (conf/set-loggers!
   ["slack-slurper"]
   {:level   :debug
    :out (org.apache.log4j.DailyRollingFileAppender.
          layout
          "/var/log/slack-slurper/slack_slurper.log"
          "'.'yyyy-MM-dd")
    }))

