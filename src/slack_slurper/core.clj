(ns slack-slurper.core
  (:gen-class
   :implements [org.apache.commons.daemon.Daemon])
  (:require [clj-slack-client.web :as slack-api]
            [clojure.tools.logging :as log]
            [slack-slurper.logging]
            [slack-slurper.slurper :as slurper]))

(slack-slurper.logging/configure-logging!)

(defn hang []
  (while true (Thread/sleep 1000)))

(defn -main [& args]
  (log/info "***** Slack Slurper Starting *****", args)
  (slurper/slurp)
  (hang))
