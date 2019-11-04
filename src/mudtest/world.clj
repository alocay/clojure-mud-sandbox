(ns mudtest.world
  (:require [clojure.data.json :as json]))

(declare world-value-reader)
(declare load-world)

(defn world-value-reader [key value]
  (if (= key :type)
    (keyword value)
    value))

(defn load-world [file]
   (json/read-str (slurp file) 
     :key-fn keyword 
     :value-fn world-value-reader))
