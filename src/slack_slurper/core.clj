(ns slack-slurper.core
  (:gen-class
   :implements [org.apache.commons.daemon.Daemon])
  (:import [org.apache.commons.daemon Daemon DaemonContext])
  (:require [clojure.tools.logging :as log]
            [slack-slurper.logging]
            [slack-slurper.slurper :as slurper]))


(def running? (atom true))

(defn init [args]
  (slack-slurper.logging/configure-logging!)
  (log/info "***** Slack Slurper Starting *****", args)
  (reset! running? true)
  (println "done init"))

(defn start []
  (log/info "starting")
  (println "starting")
  (slurper/slurp-it)
  (println "started slurper")
  (while @running?
    (println "tick")
    (log/info "tick")
    (Thread/sleep 2000)))

(defn stop []
  (reset! running? false))

(defn hang []
  (while @running? (Thread/sleep 1000)))

;; Daemon implementation

(defn -init [this ^DaemonContext context]
  (init (.getArguments context)))

(defn -start [this]
  (start))

(defn -stop [this]
  (stop))

(defn -main [& args]
  (init args)
  (start))
