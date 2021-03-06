(ns flash.routes
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [ajax.core :as ajax]
              [flash.data :refer [db]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(secretary/defroute home "/" []
  (session/put! :active-route {:page :home-page :params nil}))

(secretary/defroute reference "/:verb" {:as params}
  (do
    (swap! db assoc :active-verb-loading true)
    (ajax/GET (str "/api/verb/" (params :verb))
              {:handler (fn [res]
                          (swap! db assoc :active-verb (res "verb"))
                          (swap! db assoc :next (res "next"))
                          (swap! db assoc :prev (res "prev"))
                          (swap! db assoc :active-verb-loading false))
               :error-handler (fn []
                                (swap! db assoc :active-verb {})
                                (swap! db assoc :active-verb-loading false))})
    (session/put! :active-route {:page :reference-page :params params}))
  )

(secretary/defroute mood "/:verb/:mood" {:as params}
  (do
    (swap! db assoc :active-verb-loading true)
    (ajax/GET (str "/api/verb/" (params :verb))
              {:handler (fn [res]
                          (swap! db assoc :active-verb (res "verb"))
                          (swap! db assoc :next (res "next"))
                          (swap! db assoc :prev (res "prev"))
                          (swap! db assoc :active-verb-loading false))
               :error-handler (fn []
                                (swap! db assoc :active-verb {})
                                (swap! db assoc :active-verb-loading false))})
    (session/put! :active-route {:page :mood-page :params params}))
  )

(secretary/defroute tense "/:verb/:mood/:tense" {:as params}
  (do
    (swap! db assoc :active-verb-loading true)
    (ajax/GET (str "/api/verb/" (params :verb))
              {:handler (fn [res]
                          (swap! db assoc :active-verb (res "verb"))
                          (swap! db assoc :next (res "next"))
                          (swap! db assoc :prev (res "prev"))
                          (swap! db assoc :active-verb-loading false))
               :error-handler (fn []
                                (swap! db assoc :active-verb {})
                                (swap! db assoc :active-verb-loading false))})
    (session/put! :active-route {:page :tense-page :params params}))
  )
