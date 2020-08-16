(ns two-fifty-analytics.core
  (:require [org.httpkit.server :refer [run-server]]
            [two-fifty-analytics.data :refer [parseGameData]]))

(defn app [req]
  (let
   [str (req :query-string)]
    (println "Request Received!")
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body    (if (empty? str)
                "Invalid Request"
                (clojure.core/str (parseGameData str)))}))

(empty? "a")

(defn -main [& args]
  (run-server app {:port 8080})
  (println "Server started on port 8080"))
