(ns flash.routes
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [ajax.core :as ajax]
              [flash.data :refer [db]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(secretary/defroute home "/" []
  (session/put! :active-route {:page :home-page :params nil}))

(secretary/defroute reference "/reference/:verb" {:as params}
  (do
    (swap! db assoc :active-verb-loading true)
    (ajax/GET (str "/api/verb/" (params :verb))
              {:handler (fn [res]
                          (swap! db assoc :active-verb-loading false)
                          (swap! db assoc :active-verb res))
               :error-handler #(println "error fetching verb")})
    (session/put! :active-route {:page :reference-page :params params}))
  )
