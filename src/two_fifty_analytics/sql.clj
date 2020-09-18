(ns two-fifty-analytics.sql
  (:require [clojure.data.json :as json])
  (:require [clojure.java.jdbc :as sql])
  (:require [clojure.string :as s]))

(def db {:connection-uri
         (or (System/getenv "JDBC_DATABASE_URL")
             "jdbc:postgresql://localhost:5432/two-fifty?user=postgres&password=admin")})

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

; TOTAL NUMBERS
(defn parse-player-data
  [str]
  (let [[id name] (s/split str #",")]
    {:id id :name name}))

(defn parse-team-data
  [str]
  (map parse-player-data (s/split str #";")))

(defn add-player-score
  [score acc player]
  (update-in
   acc
   [(:id player) :score]
   +
   score))

(defn createPlayerData
  [existing-players players]
  (reduce
   (fn [acc player]
     (if (contains? acc (:id player))
       acc
       (conj acc
             [(:id player)
              {:name (:name player)
               :score 0
               :bids 0}])))
   existing-players
   players))

(defn accumulate-score-and-bids
  [acc row]
  (let [{:keys [anti_team bidding_team bid score bidder]} row
        winners (parse-team-data (if (<= bid score) bidding_team anti_team))
        playerData (createPlayerData acc (concat (parse-team-data bidding_team) (parse-team-data anti_team)))
        actual-score
        (if (<= bid score)
          bid
          (if (>= (- 250 score) 100)
            bid
            (- 250 score)))]
    (update-in
     (reduce
      (partial add-player-score actual-score)
      playerData
      winners)
     [bidder :bids]
     +
     1)))

(defn total-numbers
  []
  (let [bidderWonRounds (sql/query db ["select bidder, bid, score, bidding_team, anti_team from records"])]
    (json/write-str
     (vals
      (reduce accumulate-score-and-bids {} bidderWonRounds)))))
