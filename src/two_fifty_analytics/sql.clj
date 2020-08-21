(ns two-fifty-analytics.sql
  (:require [clojure.java.jdbc :as sql])
  (:require [clojure.string :as s]))

(def db {:connection-uri
         (str "jdbc:"
              (or (System/getenv "DATABASE_URL")
                  "postgresql://localhost:5432/two-fifty?user=postgres&password=admin"))})

(defn initialize-db
  []
  (sql/db-do-commands
   db
   (sql/create-table-ddl
    :records
    [[:id "serial" "primary key"]
     [:group_name "varchar(32)"]
     [:bidder "varchar(32)"]
     [:bid :int]
     [:score :int]
     [:trump "varchar(8)"]
     [:helper1 "varchar(13)"]
     [:helper2 "varchar(13)"]
     [:bidding_team "varchar(100)"]
     [:anti_team "varchar(100)"]
     [:date_played :timestamp "default" "current_timestamp"]]
    {:conditional? true}
    )))

(defn helperString
  [{:keys [card suit]}]
  (str card "," suit))

(defn userString
  [{:keys [id name]}]
  (str id "," name))

(defn add-row
  [{:keys [group bid score trump helpers bid-team anti-team]}]
  (sql/insert! db :records
               {:group_name group
                :bidder (:id (first bid-team))
                :bid (Integer/parseInt bid)
                :score (Integer/parseInt score)
                :trump trump
                :helper1 (if (empty? helpers)
                           ""
                           (helperString (first helpers)))
                :helper2 (if (< (count helpers) 2)
                           ""
                           (helperString (second helpers)))
                :bidding_team (s/join ";" (map userString bid-team))
                :anti_team (s/join ";" (map userString anti-team))
                }))
