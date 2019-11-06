(ns mudtest.minimap
  (:require [clojure.string :as str]))

(declare calc-range-max)
(declare convert-from-coordinate-space)
(declare get-minimap-cell)
(declare get-minimap-row)
(declare get-minimap)
(declare get-cell-property)
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
      player-mark
      (get-minimap-cell (get row x)))))

;; Creates the minimap
(defn get-minimap [posx posy world]
  (let [range-max (calc-range-max world)]
    (for [y (range (count (world :cells)))
          :let [py (convert-from-coordinate-space posy world)]]
      (get-minimap-row (convert-from-coordinate-space posx world) (get (world :cells) y) (= y py)))))

;; Gets the given property value of the cell corresponding to the player's coordinate position
(defn get-cell-property [player world prop]
  (let [x (convert-from-coordinate-space (@player :position_x) world)
        y (convert-from-coordinate-space (@player :position_y) world)]
    (get-in world [:cells y x prop])))

(defn get-cell-inhabitants [player world]
  (let [inhabs (get-cell-property player world :inhabitants)]
    (for [inhab inhabs]
      (str (inhab :name) " - " (inhab :description)))))

;; Displays the minimap
(defn display-minimap [player world]
  (let [minimap (get-minimap (@player :position_x) (@player :position_y) world)]
    (println (str "\n" (get-cell-property player world :title)))
    (println "--------------------")
    (doseq [row minimap]
      (println (str/join " " row)))
    (println "\n--------------------")
    (println (get-cell-property player world :description))
    (println "\nYou see:")
    (println "--------------------")
    (doseq [inhab (get-cell-inhabitants player world)]
      (println inhab))
    (println "")))
  



