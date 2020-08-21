(ns two-fifty-analytics.core
  (:require [org.httpkit.server :refer [run-server]]
            [compojure.core :refer [defroutes GET POST]]
            [two-fifty-analytics.data :refer [parseGameData]]
            [two-fifty-analytics.sql :refer [initialize-db add-row]])
  (:gen-class))

(defroutes app
  (POST "/" request
    (let [byte-stream (.bytes (request :body))]
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (add-row (parseGameData (apply str (map #(char (bit-and % 255)) byte-stream))))})))

(defn -main [& args]
  (initialize-db)
  (run-server app {:port 443}))
