(defproject slack-slurper "0.3.0-SNAPSHOT"
  :description "Bot for listening to and recording slack messages."
  :url "https://github.com/worace/slack-slurper"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main ^:skip-aot slack-slurper.core
  :profiles {:uberjar {:aot :all
                       :env {:log-file "/var/log/slack-slurper/slack_slurper.log"}}
             :dev     {:env {:log-file "./logs/development.log"}}
             :test    {:env {:log-file "./logs/test.log"}}}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.nrepl "0.2.10"]
                 [aleph "0.4.0"]
                 [manifold "0.1.0"]
                 [clj-slack-client "0.1.4-SNAPSHOT"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/tools.logging "0.3.1"]
                 [cheshire "5.5.0"]
                 [clojurewerkz/elastisch "2.1.0"]
                 [environ "1.0.1"]
                 [clj-logging-config "1.9.12"]]
  :plugins [[lein-environ "1.0.1"]]
)
