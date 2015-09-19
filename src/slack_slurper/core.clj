(ns slack-slurper.core
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [slack-slurper.logging]
            [slack-slurper.connection :as conn]
            [slack-slurper.listener :as l]
            [slack-slurper.heartbeat :as hb]
            [clojure.tools.nrepl.server :as repl]
            [slack-slurper.slurper :as slurper]))

(def running? (atom true))
(def repl-server (atom nil))

(defn start-repl! []
  (if @repl-server
    (repl/stop-server @repl-server))
  (reset! repl-server (repl/start-server :port 7888))
  (.addShutdownHook (Runtime/getRuntime) (Thread. (fn [] (repl/stop-server @repl-server)))))

(defn start []
  (let [c (conn/ws-stream)]
    (l/listen c #(log/info %) running?)
    (hb/heartbeat c hb/message running?))
  (log/info "**** Started slurper. ****"))

(defn stop [] (reset! running? false))

(defn -main [& args]
  (slack-slurper.logging/configure-logging!)
  (start-repl!)
  (reset! running? true)
  (log/info "***** Slack Slurper Starting *****", args)
  (start))
