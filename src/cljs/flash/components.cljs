(ns flash.components
    (:require [reagent.core :as reagent :refer [atom]]
              [clojure.string :refer [starts-with? replace-first]]
              [reagent.session :as session]
              [flash.routes :refer [reference]]
              [flash.components.omni-select :refer [omni-select]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(def verbs
  {"comer" {"presento" {"yo" "como" "tu" "comes"}
            "indefinido" {"yo" "comi" "tu" "comiste"}}
   "habar" {"present" {"yo" "hablo" "tu" "hablas"}}
   "hablar" {"present" {"yo" "hablo" "tu" "hablas"}}
   "habars" {"present" {"yo" "hablo" "tu" "hablas"}}
   "hablarss" {"present" {"yo" "hablo" "tu" "hablas"}}
   "hablarsss" {"present" {"yo" "hablo" "tu" "hablas"}}
   "hablarssss" {"present" {"yo" "hablo" "tu" "hablas"}}
   "hablarsssss" {"present" {"yo" "hablo" "tu" "hablas"}}
   })

(defn verb-list [verbs]
  [:ul
   (for [[subject verb] verbs]
     [:li {:key subject}
      [:span subject]
      [:span verb]])])

(defn home-page [])

(defn reference-page []
  (let [verb (get-in (session/get :active-route) [:params :verb])]
    [:div [:h2 verb]
     (for [[tense tenses] (verbs verb)]
       [:div {:key (str verb "-" tense)}
       [:h3 tense]
       [verb-list tenses]])]))


(defn current-page []
  (let [{:keys [page params]} (session/get :active-route)
        pages {:home-page home-page
               :reference-page reference-page}
        component (pages page)]
    (if component
      [:div [component params]]
      [:div "sorry page not found"])))

(defn on-change [value]
  (accountant/navigate! (reference {:verb value})))

(defn app []
  [:div
   [omni-select verbs on-change]
   [current-page]])
