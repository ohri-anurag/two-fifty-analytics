(ns two-fifty-analytics.data
  (:require [clojure.string :as str]))

(defn parsePlayerData
  "Takes a string of the form <Player id>:<Player name>"
  [str]
  (let [[id name] (str/split str #":")]
    {:id id :name name}))

(defn parseTeamData
  "Takes a string of the form <Player 1 Data>,<Player 1 Data>,..,<Player 1 Data>
   Each of the player data must satisfy parsePlayerData
   "
  [str]
  (map parsePlayerData (str/split str #",")))

(defn parseGameData
  "Takes a string of the form <Bidding Data>;<Anti Team Data>
   Each of the team data must satisfy parseTeamData
   "
  [str]
  (let [[bid-team anti-team] (map parseTeamData (str/split str #";"))]
    {:bid-team bid-team :anti-team anti-team}))

(parseGameData "ohri:Anurag,gupta:avikant,jain1:navneet;jain2:apoorv,jain3:devendra,s:yogesh")
;; (clojure.repl/doc parsePlayerData)