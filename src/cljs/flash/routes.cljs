(ns flash.routes
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [ajax.core :as ajax]
              [flash.data :refer [db]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(secretary/defroute home "/" []
  (session/put! :active-route {:page :home-page :params nil})
  )

(secretary/defroute reference "/reference/:verb" {:as params}
  (do
    (ajax/GET (str "/api/verb/" (params :verb)))
              {:handler #(swap! db assoc :active-verb %)
               :error-handler #(println "error fetching verb")}
    (session/put! :active-route {:page :reference-page :params params}))
  )
