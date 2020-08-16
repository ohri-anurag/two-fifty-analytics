(ns two-fifty-analytics.core
  (:require [org.httpkit.server :refer [run-server]]
            [compojure.core :refer [defroutes GET POST]]
            [two-fifty-analytics.data :refer [parseGameData]]))

(defroutes app
  (POST "/" request
    (let [byte-stream (.bytes (request :body))]
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (clojure.core/str (parseGameData (apply str (map #(char (bit-and % 255)) byte-stream))))})))

(defn -main [& args]
  (run-server app {:port 8080})
  (println "Server started on port 8080"))
