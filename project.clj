(defproject slack-slurper "0.1.0"
  :description "Bot for listening to and recording slack messages."
  :url "https://github.com/worace/slack-slurper"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main slack-slurper.core
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [aleph	"0.4.0"]
                 [manifold "0.1.0"]
                 [clj-slack-client "0.1.4-SNAPSHOT"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/tools.logging "0.3.1"]
                 [clj-logging-config "1.9.12"]
                 ])
