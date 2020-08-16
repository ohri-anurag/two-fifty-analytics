(ns two-fifty-analytics.sql
  (:require [clojure.java.jdbc :as sql]))

(def db {:connection-uri
         (or (System/getenv "DATABASE_URL")
             "jdbc:postgresql://localhost:5432/two-fifty?user=postgres&password=admin")})

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
   [:date_played :timestamp "default" "current_timestamp"]]))