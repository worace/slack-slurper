(ns slack-slurper.core
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [slack-slurper.logging]
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
  (slurper/slurp-it)
  (log/info "**** Started slurper. Will begin wait loop. ****")
  (while @running?
    (Thread/sleep 2000)))

(defn stop [] (reset! running? false))

(defn -main [& args]
  (slack-slurper.logging/configure-logging!)
  (start-repl!)
  (reset! running? true)
  (log/info "***** Slack Slurper Starting *****", args)
  (start))
