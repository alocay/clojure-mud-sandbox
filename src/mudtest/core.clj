(ns mudtest.core
  (:gen-class)
  (:require [clojure.string :as str]))

(def player (atom {:position_x 0 :position_y 0 :hp 100 :mana 100}))
(def minimap-min -10)
(def minimap-max 10)
(def environment {:min -10 :max 10})
(def empty-mark ".")
(def player-mark "@")
(def range-min 1)
(def range-max (+ (Math/abs minimap-min) minimap-max))

(declare process-input!)
(declare display-minimap)
(declare get-minimap-row)
(declare get-minimap)

(defn get-user-input [] 
  (do (println "\nInput: ") (flush) (process-input! (read-line) player)))

(defn display-player-info []
  (println (str 
             "\nPlayer | HP: " (@player :hp) 
             " | MP: " (@player :mana) 
             " | (" (@player :position_x) ", " (@player :position_y) ")")))

(defn get-player-minimap-x [player_x]
  (let [oldRange (- minimap-max minimap-min)
        newRange (- range-max range-min)]
    (+ (/ (* (- player_x minimap-min) newRange) oldRange) range-min)))  

(defn get-minimap-row [player_x]
  (if (or (nil? player_x) (< player_x min) (> player_x max))
    (take 20 (repeat empty-mark))
    (let [x (get-player-minimap-x player_x)]
      (conj (take (- x 1) (repeat empty-mark)) player-mark (take (- range-max x) (repeat empty-mark))))))

(defn get-minimap [posx posy]
  (for [y (range 1 (+ range-max 1)) 
        :let [py (get-player-minimap-x posy)]]

    (if (= y py)
      (get-minimap-row py)
      (get-minimap-row nil))))

(defn display-minimap []
  (let [minimap (get-minimap (@player :position_x) (@player :position_y))]
    (doseq [row minimap]
      (println (str/join " " row)))))

(defn process-input! [in play]
  (let [cmd (str (first (str/lower-case in)))]
    (cond 
      (= cmd "n") (swap! play update :position_y inc)
      (= cmd "s") (swap! play update :position_y dec)
      (= cmd "w") (swap! play update :position_x dec)
      (= cmd "e") (swap! play update :position_x inc)
      (= cmd "q") (System/exit 0)
      :else "Unknown cmd")))

(defn -main []
  (while true
    (display-minimap)
    (display-player-info)
    (get-user-input)))


