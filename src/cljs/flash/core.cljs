(ns flash.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [flash.routes]
              [flash.components :refer [app]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(defn mount-root []
  (reagent/render [app] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
