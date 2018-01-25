(ns tx-monitor.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clj-http.client :as client]
            [cheshire.core :refer :all]
            [postal.core :refer [send-message]])
  (:gen-class))

(defn any-tx-since-time?
  [mins-ago]
  (let [since-unixtime (- (quot (System/currentTimeMillis) 1000)
                          (* 60 mins-ago))]
    (->> (client/get (str "http://api.etherscan.io/api?"
                          "module=account&action=txlist"
                          "&address=0xac838aee2f650a6b970ecea56d4651653c1f84a1"
                          "&startblock=0&endblock=99999999&sort=desc"
                          "&page=1&offset=10"
                          "&apikey=BJ5DTZCCTNSZ634QWKI37BBVHDPF2ZURDN")
                     {:accept :json})
         :body
         (#(parse-string % true))
         :result
         (map #(select-keys % [:hash :timeStamp]))
         (filter (comp (partial < since-unixtime)
                       #(Integer. %)
                       :timeStamp))
         count
         pos?)))

(defn alert-if-no-tx-in-last-hour []
  (when-not (any-tx-since-time? 60)
    (send-message {:host "smtp.gmail.com"
                   :user "clearcoinalerts@gmail.com" ; be sure to mark this address as "not spam"
                   :pass "JDHH76s$%^^HgbcnK"
                   :ssl :yes!!!11}
                  {:from "clearcoinalerts@gmail.com"
                   :to "elwell.christopher@gmail.com"
                   :subject "No Recent CLR Transactions"
                   :body (str "This is an alert that there have not been any CLR "
                              "token transactions in the last hour.")})))

(def cli-options
  [["-e"
    :id :email-address]
   ["-t"
    :id :num-of-minutes
    :default 60
    :parse-fn #(Integer/parseInt %)]])

(defn -main
  [& args]
  (println (parse-opts args cli-options))
  ;; (alert-if-no-tx-in-last-hour)
  )
