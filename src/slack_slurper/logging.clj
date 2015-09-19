(ns slack-slurper.logging
  (:require [clojure.tools.logging :as log]
            [environ.core :refer [env]]
            [clj-logging-config.log4j :as conf]))

(def layout (org.apache.log4j.PatternLayout. "%d{ISO8601} %-5p %c - %m%n"))

(defn configure-logging!
  ([] (configure-logging! (env :log-file)))
  ([logfile]
   (conf/set-loggers!
    ["slack-slurper"]
    {:level :debug
     :out (org.apache.log4j.DailyRollingFileAppender.
           layout
           logfile
           "'.'yyyy-MM-dd")
     })))

