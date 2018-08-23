(defproject tx-monitor "0.1.0-SNAPSHOT"
  :description "Monitor an Ethereum address; notify via email if no transactions within given period of time."  
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "3.9.1"]
                 [cheshire "5.8.0"]
                 [com.draines/postal "2.0.2"]
                 [org.clojure/tools.cli "0.3.7"]]
  :main tx-monitor.core
  :target-path "target/%s")
