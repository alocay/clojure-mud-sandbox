(ns mudtest.world
  (:require [clojure.data.json :as json]))

(declare world-value-reader)
(declare load-world)

(defn world-value-reader [key value]
  (cond 
    (= key :type) (keyword value)
    :else value))

(defn wilsfunc [foo]
  (for [x [1 2 3]]
    (println x)))

(defn load-world [file]
   (json/read-str (slurp file) 
     :key-fn keyword 
     :value-fn world-value-reader))
