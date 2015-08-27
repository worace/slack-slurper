(ns slack-slurper.core
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [slack-slurper.logging]
            [slack-slurper.slurper :as slurper]))

(def running? (atom true))

(defn start []
  (slurper/slurp-it)
  (while @running?
    (log/info "tick")
    (Thread/sleep 2000)))

(defn stop [] (reset! running? false))

(defn -main [& args]
  (slack-slurper.logging/configure-logging!)
  (reset! running? true)
  (log/info "***** Slack Slurper Starting *****", args)
  (start))
