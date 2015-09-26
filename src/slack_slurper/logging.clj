(ns slack-slurper.logging
  (:require [clojure.tools.logging :as log]
            [environ.core :refer [env]]
            [clj-logging-config.log4j :as conf]))

(def layout (org.apache.log4j.PatternLayout. "%d{ISO8601} %-5p %c - %m%n"))

(defn log-file [] (clojure.string/join "/" [(env :log-dir) "slack_slurper.log"]))

(defn configure-logging!
  ([] (configure-logging! (log-file)))
  ([logfile]
   (conf/set-loggers!
    ["slack-slurper"]
    {:level :debug
     :out (org.apache.log4j.DailyRollingFileAppender.
           layout
           logfile
           "'.'yyyy-MM-dd")
     })))

