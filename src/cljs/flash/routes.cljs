(ns flash.routes
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(secretary/defroute home "/" []
  (session/put! :active-route {:page :home-page :params nil}))

(secretary/defroute reference "/reference/:verb" {:as params}
  (session/put! :active-route {:page :reference-page :params params}))
