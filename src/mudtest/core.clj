(ns mudtest.core
  (:gen-class)
  (:require [clojure.string :as str])
  (:require [clojure.java.io :as io])
  (:require [clojure.data.json :as json]))

(def player (atom {:position_x 0 :position_y 0 :hp 100 :mana 100}))
(def world {})
(def minimap-min -5)
(def minimap-max 5)
(def environment {:min -10 :max 10})
(def empty-mark ".")
(def player-mark "@")
(def range-min 0)
(def range-max (+ (Math/abs minimap-min) minimap-max))
(def world-file (io/resource "world.json"))

(declare process-input!)
(declare display-minimap)
(declare get-minimap-row)
(declare get-minimap)
(declare load-world!)
(declare print-flush)

(defn get-user-input [] 
  (do (print-flush "\nInput: ") (process-input! (read-line) player)))

(defn display-player-info []
  (println (str 
             "\nPlayer | HP: " (@player :hp) 
             " | MP: " (@player :mana) 
             " | (" (@player :position_x) ", " (@player :position_y) ")")))

(defn convert-from-coordinate-space [player_x_or_y]
  (let [oldRange (- minimap-max minimap-min)
        newRange (- range-max range-min)]
    (+ (/ (* (- player_x_or_y minimap-min) newRange) oldRange) range-min)))

(defn get-minimap-row [player_x]
  (if (or (nil? player_x) (< player_x minimap-min) (> player_x minimap-max))
    (take range-max (repeat empty-mark))
    (let [x (convert-from-coordinate-space player_x)]
      (flatten (conj (take x (repeat empty-mark)) player-mark (take (- (- range-max x) 1) (repeat empty-mark)))))))

(defn get-minimap [posx posy]
  (for [y (range 1 (+ range-max 1)) 
        :let [py (convert-from-coordinate-space posy)]]
    (if (= y py)
      (get-minimap-row posx)
      (get-minimap-row nil))))

(defn display-minimap []
  (let [minimap (get-minimap (@player :position_x) (@player :position_y))]
    (doseq [row minimap]
      (println (str/join " " row)))))

(defn process-input! [in play]
  (let [cmd (str (first (str/lower-case in)))]
    (cond 
      (= cmd "n") (swap! play update :position_y dec)
      (= cmd "s") (swap! play update :position_y inc)
      (= cmd "w") (swap! play update :position_x inc)
      (= cmd "e") (swap! play update :position_x dec)
      (= cmd "q") (System/exit 0)
      :else "Unknown cmd")))

(defn load-world! []
  (do 
    (print-flush "Loading world...")
    (let [worldData (json/read-str (slurp world-file) :key-fn keyword)]
      (def world worldData)
      (def minimap-max (world :max))
      (def minimap-min (world :min)))
    (print-flush "Loaded!")))

;; Fix this world loading (min/max)
(defn print-flush [msg] (do (println msg) (flush)))

(defn -main []
  (load-world!)
  (while true
    (display-minimap)
    (display-player-info)
    (get-user-input)))


