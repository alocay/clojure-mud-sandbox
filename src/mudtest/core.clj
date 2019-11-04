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

(defn -main []
  (let [world (world/load-world world-file)]
    (while true
      (minimap/display-minimap player world)
      (display-player-info)
      (get-user-input))))


