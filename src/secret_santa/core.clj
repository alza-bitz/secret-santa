(ns secret-santa.core
  (:require [clojure.string :as str]
            [clojure.edn :as edn])
  (:gen-class))

(defn users
  "Returns the users who will be taking part in the secret santa"
  []
  (edn/read-string (slurp "users.edn")))

(defn- pairs
  "Return unique pairings of users consisting of a giver and a receiver"
  [users]
  (->> users
       shuffle
       cycle
       (take (+ 1 (count users)))
       (partition 2 1)
       (map #(hash-map
              :giver (first %)
              :receiver (second %)))))

(defn- message
  "Returns a formatted message intended for the giver"
  [giver receiver]
  (format (slurp "giver.template")
          (:email giver) (:name giver) (:gender giver) (:name receiver)))

(defn -main
  [& args]
  (doseq [{:keys [giver receiver]} (pairs (users))]
    (spit (str (str/lower-case (:name giver)) ".txt") 
          (message giver receiver))))