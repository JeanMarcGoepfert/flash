(ns flash.components
    (:require [reagent.core :as reagent :refer [atom]]
              [clojure.string :refer [starts-with?]]
              [reagent.session :as session]
              [flash.routes :refer [reference]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(def verbs
  {"comer" {"presento" {"yo" "como" "tu" "comes"}
            "indefinido" {"yo" "comi" "tu" "comiste"}}
   "habar" {"present" {"yo" "hablo" "tu" "hablas"}}
   "hablar" {"present" {"yo" "hablo" "tu" "hablas"}}
   })


(def db
  (reagent/atom {:verb-input ""}))


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
  (let [{:keys [component params]} (session/get :active-route)]
    (if component
      [:div [component params]]
      [:div "sorry page not found"])))

(defn submit-handler [e]
  (.preventDefault e)
  (secretary/dispatch! (reference {:verb (@db :verb-input)}))
  (swap! db assoc :verb-input ""))

(defn change-handler [value]
  (swap! db assoc :verb-input value))

(defn filter-verbs [input-value]
  (filter #(starts-with? % input-value) (keys verbs)))

(defn handle-focus [verb]
  (swap! db assoc :verb-input verb))

(defn suggestion-box [input-value]
  (if (> (count input-value) 0)
    (let [matches (filter-verbs input-value)]
      [:ul
       (for [verb matches]
         [:li {:key verb :tab-index 0 :on-focus #(handle-focus verb)} verb])])))

(defn verb-search []
  (let [input-value (@db :verb-input)]
    [:form {:on-submit #(submit-handler %)}
     [:input {:on-change #(change-handler (-> % .-target .-value))
              :auto-focus true
              :value input-value
              :tab-index 0
              :placeholder "enter verb"}]
     ]))

(defn app []
  [:div
   [verb-search]
   [current-page]])
