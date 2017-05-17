(ns flash.components
    (:require [reagent.core :as reagent :refer [atom]]
              [clojure.string :refer [starts-with? replace-first]]
              [reagent.session :as session]
              [flash.data :refer [db]]
              [flash.routes :refer [reference]]
              [flash.components.omni-select :refer [omni-select]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(def verbs
  {"comer" {"presento" {"yo" "como"
                        "tu" "comes"
                        "el" "come"
                        "nosotros" "comemos"
                        "vosotros" "comeis"
                        "ellos" "comen"}
            "indefinido" {"yo" "comi" "tu" "comiste"}
            "sindefinido" {"yo" "comi" "tu" "comiste"}
            "ssindefinido" {"yo" "comi" "tu" "comiste"}
            "sssindefinido" {"yo" "comi" "tu" "comiste"}
            "isddndefinido" {"yo" "comi" "tu" "comiste"}}
   "habar" {"present" {"yo" "hablo" "tu" "hablas"}}
   "hablar" {"present" {"yo" "hablo" "tu" "hablas"}}
   "habars" {"present" {"yo" "hablo" "tu" "hablas"}}
   "hablarss" {"present" {"yo" "hablo" "tu" "hablas"}}
   "hablarsss" {"present" {"yo" "hablo" "tu" "hablas"}}
   "hablarssss" {"present" {"yo" "hablo" "tu" "hablas"}}
   "hablarsssss" {"present" {"yo" "hablo" "tu" "hablas"}}
   })

(defn verb-list [verbs]
  (let [[first-3 last-3] (partition-all 3 verbs)]
    [:div
     [:ul.tense-column
      (for [[subject verb] first-3]
        [:li.tense-row {:key subject}
         [:span (str subject " " verb)]])]
     [:ul.tense-column
      (for [[subject verb] last-3]
        [:li.tense-row {:key subject}
         [:span (str subject " " verb)]
         ])]]))

(defn home-page [])

(defn reference-page []
  (let [verb (get-in (session/get :active-route) [:params :verb])]
    [:div [:h2 verb]
     [:div
      (for [[tense tenses] (verbs verb)]
        [:div.verb-card {:key (str verb "-" tense)}
         [:h3 [:a {:href "#"} tense]]
         [verb-list tenses]])]]))

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
  [:div.page-wrapper
   [omni-select (@db :verb-list) on-change]
   [current-page]])
