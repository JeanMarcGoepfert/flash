(ns flash.core
    (:require [reagent.core :as reagent]
              [flash.components :refer [app]]
              [flash.data :refer [db]]
              [ajax.core :as ajax]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(defn handler [res]
  (swap! db assoc :verb-list res))

(defn error-handler [err]
  (println "err"))

(defn fetch-initial-data []
  (ajax/GET "/api/verb-list"
            {:handler handler
             :error-handler error-handler}))

(defn mount-root []
  (reagent/render [app] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler #(secretary/dispatch! %)
     :path-exists?  #(secretary/locate-route %)})
  (accountant/dispatch-current!)
  (fetch-initial-data)
  (mount-root))
