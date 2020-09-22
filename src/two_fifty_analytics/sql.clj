(ns two-fifty-analytics.sql
  (:require [clojure.data.json :as json])
  (:require [clojure.java.jdbc :as sql])
  (:require [clojure.string :as s]))

(def db {:connection-uri
        ;;  "jdbc:postgresql://ec2-34-251-118-151.eu-west-1.compute.amazonaws.com:5432/d1mavs46gvif3m?user=qndtnnugfgwkyi&password=c5e3d4971b990d7916d0c697edb3da995daf9912c05ef291646d17c36f23f61c"})
         (or (System/getenv "JDBC_DATABASE_URL")
             "jdbc:postgresql://localhost:5432/two-fifty?user=postgres&password=admin")})

(defn initialize-db
  []
  (sql/db-do-commands
   db
   (sql/create-table-ddl
    :games
    [[:id "serial" "primary key"]
     [:group_name "varchar(11)"]
     [:bid :smallint]
     [:trump "varchar(11)"]
     [:helper1 "varchar(15)"]
     [:helper2 "varchar(15)"]
     [:score :smallint]
     [:bidder :integer]
     [:bidding_team :integer]
     [:anti_team :integer]
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
  (let [players (into {}
                      (map (fn [p] [(:player_id p) (:id p)])
                           (sql/query db "select id, player_id from players")))
        bid_team (map (comp #(get players %1) :id) bid-team)
        anti_team (map (comp #(get players %1) :id) anti-team)
        ]
    ;; Insert both teams into teams table
    (sql/db-do-commands db
                        (str "insert into teams(p1,p2,p3,p4,p5) values "
                             (s/join "," (map (fn [t] (str "(" (s/join "," (concat t (repeat (- 5 (count t)) 0))) ")")) [bid_team anti_team]))
                             " on conflict do nothing"))
    (let [teams (into {}
                      (map (fn [t] [(filter #(> %1 0) [(:p1 t) (:p2 t) (:p3 t) (:p4 t) (:p5 t)])
                                    (:id t)])
                           (sql/query db "select * from teams")))
          bid_team_id (get teams bid_team)
          anti_team_id (get teams anti_team)
          bidder (first bid_team)]
      ;; Insert the game details into games table
      (sql/insert! db :games
                   {:group_name group
                    :bid (Integer/parseInt bid)
                    :trump trump
                    :helper1 (if (empty? helpers)
                               ""
                               (helperString (first helpers)))
                    :helper2 (if (< (count helpers) 2)
                               ""
                               (helperString (second helpers)))
                    :score (Integer/parseInt score)
                    :bidder bidder
                    :bidding_team bid_team_id
                    :anti_team anti_team_id
                    }))
    ))

(defn add-players
  [playerDataString]
  (let [playerData (json/read-str playerDataString)
        valueString (map
                     (fn [p]
                       (str "("
                            (s/join ","
                                    (map #(str "'" %1 "'") p))
                            ")")
                       ) playerData)]
    (sql/db-do-commands db
                       (str "insert into players(player_name, player_id) values "
                            (s/join "," valueString)
                            " on conflict do nothing")
                        )
    ))

; TOTAL NUMBERS
(defn total-numbers
  []
  (let [results (sql/query db [(slurp "src/two_fifty_analytics/sql/total.pgsql")])]
    (json/write-str results)))
