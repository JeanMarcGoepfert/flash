(ns flash.routes
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [flash.components :refer [home-page reference-page]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(secretary/defroute home "/" []
  (session/put! :active-route {:component #'home-page :params nil}))

(secretary/defroute reference "/reference/:verb" {:as params}
  (session/put! :active-route {:component #'reference-page :params params}))
