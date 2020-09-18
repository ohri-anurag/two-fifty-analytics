(ns two-fifty-analytics.core
  (:require [org.httpkit.server :refer [run-server]]
            [compojure.core :refer [defroutes GET POST]]
            [two-fifty-analytics.data :refer [parseGameData]]
            [two-fifty-analytics.sql :refer [initialize-db add-row total-numbers]])
  (:gen-class))

(defroutes app
  (POST "/" request
    (let [byte-stream (.bytes (request :body))]
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (add-row (parseGameData (apply str (map #(char (bit-and % 255)) byte-stream))))}))
  (GET "/total" []
    {:status 200
     :headers {"Content-Type" "application/json"
               "Access-Control-Allow-Origin" "*"
               }
     :body (total-numbers)}))

(defn -main [& args]
  (initialize-db)
  (run-server app {:port (Integer/parseInt (or (System/getenv "PORT") "8081"))}))
