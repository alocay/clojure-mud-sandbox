(ns mudtest.core
  (:gen-class)
  (:require [clojure.string :as str])
  (:require [clojure.java.io :as io])
  (:require [mudtest.minimap :as minimap])
  (:require [mudtest.world :as world]))

(declare get-user-input)
(declare display-player-info)
(declare process-input!)
(declare print-flush)
(declare generate-mob)
(declare generate-mobs)

(def player (atom {:position_x 0 :position_y 0 :hp 100 :mana 100}))
(def world-file (io/resource "world.json"))

(defn get-user-input [] 
  (do (print-flush "\nInput: ") (process-input! (read-line) player)))

(defn display-player-info []
  (println (str 
             "\nPlayer | HP: " (@player :hp) 
             " | MP: " (@player :mana) 
             " | (" (@player :position_x) ", " (@player :position_y) ")")))

(defn process-input! [in play]
  (let [cmd (str (first (str/lower-case in)))]
    (cond 
      (= cmd "n") (swap! play update :position_y dec)
      (= cmd "s") (swap! play update :position_y inc)
      (= cmd "w") (swap! play update :position_x dec)
      (= cmd "e") (swap! play update :position_x inc)
      (= cmd "q") (System/exit 0)
      :else (println "Unknown cmd"))))

(defn print-flush [msg] (do (println msg) (flush)))

(defn generate-mob []
  (let [x (rand)]
    (if (> x 0.5) 
      {:name "A shiny bug" :description "A bug with a jewel-like carapace" :hp 100}
      {:name "A cowering dog" :description "A dog hunching over with his tail between his legs" :hp 100})))

(defn generate-mobs [world]
  (doseq [y (range (count (@world :cells)))
          x (range (count (get-in @world [:cells y])))]
    (let [mob (generate-mob)]
      (swap! world assoc-in [:cells y x :inhabitants] (conj (get-in @world [:cells y x :inhabitants]) mob)))))    

(defn -main []
  (let [world (atom (world/load-world world-file))]
    (generate-mobs world)
    (while true
      (minimap/display-minimap player @world)
      (display-player-info)
      (get-user-input))))


