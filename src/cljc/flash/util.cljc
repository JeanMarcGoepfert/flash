(ns flash.util
  (:require [clojure.string :refer [replace]]))

(defn str->dashcase [str]
  (replace str #" " "-"))

(defn dashcase->str [str]
  (replace str #"-" " "))
