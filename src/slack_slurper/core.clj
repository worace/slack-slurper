(ns slack-slurper.core
  (:gen-class
   :implements [org.apache.commons.daemon.Daemon])
  (:import [org.apache.commons.daemon Daemon DaemonContext])
  (:require [clojure.tools.logging :as log]
            [slack-slurper.logging]
            [slack-slurper.slurper :as slurper]))

(slack-slurper.logging/configure-logging!)

(def running? (atom true))

(defn init [args]
  (log/info "***** Slack Slurper Starting *****", args)
  (reset! running? true))

(defn start []
  (slurper/slurp-it)
  (while @running?
    (Thread/sleep 2000)))

(defn stop []
  (reset! running? false))

(defn hang []
  (while @running? (Thread/sleep 1000)))

;; Daemon implementation

(defn -init [this ^DaemonContext context]
  (init (.getArguments context)))

(defn -start [this]
  (future (start)))

(defn -stop [this]
  (stop))

(defn -main [& args]
  (init args)
  (start))
