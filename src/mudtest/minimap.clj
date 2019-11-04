(ns mudtest.minimap
  (:require [clojure.string :as str]))

(declare calc-range-max)
(declare convert-from-coordinate-space)
(declare get-minimap-cell)
(declare get-minimap-row)
(declare get-minimap)
(declare display-minimap)

(def empty-mark ".")
(def player-mark "@")
(def unknown-mark "?")

;; Calculates the maximum of the array space range
(defn calc-range-max [world]
  (+ (Math/abs (world :min)) (world :max)))

;; Convets a minimap cell x or y coordinate to array space
(defn convert-from-coordinate-space [player_x_or_y world]
  (let [minimap-max (world :max)
        minimap-min (world :min)
        range-min 0
        range-max (calc-range-max world)
        oldRange (- minimap-max minimap-min)
        newRange (- range-max range-min)]
    (+ (/ (* (- player_x_or_y minimap-min) newRange) oldRange) range-min)))

;; Creates a single minimap cell
(defn get-minimap-cell [cell]
  (cond
    (= (cell :type) :wilderness) empty-mark
    :else unknown-mark))

;; Creates a single minimap row
(defn get-minimap-row [player_x row is-player-row]
  (for [x (range (count row))]
    (if (and is-player-row (= x player_x))
      "@"
      (get-minimap-cell (get row x)))))

;; Creates the minimap
(defn get-minimap [posx posy world]
  (let [range-max (calc-range-max world)]
    (for [y (range (count (world :cells)))
          :let [py (convert-from-coordinate-space posy world)]]
      (get-minimap-row (convert-from-coordinate-space posx world) (get (world :cells) y) (= y py)))))

;; Displays the minimap
(defn display-minimap [player world]
  (let [minimap (get-minimap (@player :position_x) (@player :position_y) world)]
    (doseq [row minimap]
      (println (str/join " " row)))))



