(ns flash.data
    (:require [reagent.core :as reagent :refer [atom]]))

(def db-defaults {:verb-list []
                  :prev nil
                  :next nil
                  :active-verb-loading false
                  :active-verb {}})

(def db (reagent/atom db-defaults))
