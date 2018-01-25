(defproject tx-monitor "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "3.7.0"]
                 [cheshire "5.8.0"]
                 [com.draines/postal "2.0.2"]]
  :main ^:skip-aot tx-monitor.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
