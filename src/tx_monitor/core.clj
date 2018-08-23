(ns tx-monitor.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clj-http.client :as client]
            [cheshire.core :refer [parse-string]]
            [postal.core :refer [send-message]]))

(defn any-tx-since-time?
  [address mins-ago & {:keys [api-key]}]
  (->> (client/get (str "http://api.etherscan.io/api?module=account&action=txlist"
                        "&address=" address
                        "&startblock=0&endblock=99999999&sort=desc&page=1&offset=10"
                        (when api-key (str "&apikey=" api-key)))
                   {:accept :json})
       :body
       (#(parse-string % true))
       :result
       (map #(select-keys % [:hash :timeStamp]))
       (filter (comp (partial < (- (quot (System/currentTimeMillis) 1000) (* 60 mins-ago)))
                     #(Integer. %)
                     :timeStamp))
       count
       pos?))

(defn alert-if-no-tx-since-time
  [address mins-ago & {:keys [api-key gmail-cred notify-email]}]
  (when-not (any-tx-since-time? address mins-ago
                                :api-key api-key)
    (send-message (merge {:host "smtp.gmail.com"
                          :ssl :yes!!!11}
                         gmail-cred)
                  {:from (:user gmail-cred)
                   :to notify-email
                   :subject "No Recent Transactions"
                   :body (str "This is an alert that there have not been any "
                              "transactions in the past " mins-ago " minutes "
                              "on address " address ".")})))

(defn -main
  []
  (alert-if-no-tx-since-time
   "0x1E26b3D07E57F453caE30F7DDd2f945f5bF3EF33"   ;; Ethereum address to track
   20                                             ;; notify email if no tx in last 20 minutes
   :api-key "BJ5DTZCCTNSZ634QWKI37BBVHDPF2ZURDN"  ;; API Key for Etherscan (optional)
   :gmail-cred {:user "example@gmail.com"         ;; Gmail login info for sending out the email
                :pass "examplepassword"}
   :notify-email "info@example.com"))             ;; where to send notification email to
