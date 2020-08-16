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

(defn parseTeamsData
  "Takes a string of the form <Bidding Team Data>;<Anti Team Data>
   Each of the team data must satisfy parseTeamData
   "
  [str]
  (let [[bid-team anti-team] (map parseTeamData (str/split str #"\|"))]
    {:bid-team bid-team :anti-team anti-team}))

(defn parseScoreData
  [str]
  (let [[bid score] (str/split str #",")]
    {:bid bid :score score}))

(defn parseHelper
  [str]
  (let [[card suit] (str/split str #":")]
    {:card card :suit suit}))

(defn parseHelperData
  [str]
  (map parseHelper (str/split str #",")))

(defn parseTrumpData
  [str]
  (let [[trump helperData] (str/split str #"\|")]
    {:trump trump :helpers (parseHelperData helperData)}))

(defn parseGameData
  [str]
  (let [[group scoreData trumpData teamData] (str/split str #";")]
    (into {:group group} [(parseScoreData scoreData), (parseTrumpData trumpData), (parseTeamsData teamData)])))

;; (parseGameData "a;180,190;diamond|ace:diamond,ace:club;ohri:Anurag,gupta:avikant,jain1:navneet|jain2:apoorv,jain3:devendra,s:yogesh")
; (parseTeamsData "ohri:Anurag,gupta:avikant,jain1:navneet|jain2:apoorv,jain3:devendra,s:yogesh")
;; (clojure.repl/doc parsePlayerData)