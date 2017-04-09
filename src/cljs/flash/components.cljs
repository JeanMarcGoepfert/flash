(ns flash.components
    (:require [reagent.core :as reagent :refer [atom]]
              [clojure.string :refer [starts-with? replace-first blank?]]
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
  (reagent/atom {:verb-input ""
                 :suggestions []
                 :verb-input-component nil
                 :active-suggestion ""
                 }))

(defn filter-verbs [input-value]
  (filter #(starts-with? % input-value) (keys verbs)))

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

(defn submit-handler [e input-reference]
  (let [suggestion (@db :active-suggestion)
        value (@db :verb-input)
        path (if (blank? suggestion)
               value
               suggestion)]
  (do
    (.preventDefault e)
    (accountant/navigate! (reference {:verb path}))
    (swap! db assoc :verb-input "")
    (swap! db assoc :verb-suggestion "")
    (swap! db assoc :active-suggestion "")
    (.focus @input-reference))))

(defn change-handler [value]
  (do
    (swap! db assoc :verb-input value)
    (swap! db assoc :suggestions (filter-verbs value))
    (if (blank? (@db :verb-input))
      (swap! db assoc :active-suggestion "")
      (swap! db assoc :active-suggestion (or (first (@db :suggestions)) "")))))

(defn handle-focus [verb]
  (swap! db assoc :active-suggestion verb))

(defn suggestion-box [input-value]
  (if (> (count input-value) 0)
    (let [matches (@db :suggestions)]
      [:ul
       (for [verb matches]
         [:li {:key verb}
          [:input {:on-focus #(handle-focus verb)
                   :value verb
                   :read-only true}]])])))

(defn verb-search []
  (let [input-value (@db :verb-input)
        input-suggestion (@db :active-suggestion)
        input-ref (reagent/atom nil)]
    [:form.verb-search {:on-submit #(submit-handler % input-ref)}
     [:div.input-wrapper
      [:span.suggestion-wrapper
       [:span.suggestion-text.common-suggestion input-value]
       [:input.suggestion-text.remaining-suggestion {:value (replace-first input-suggestion (re-pattern input-value) "")}]]
      [:input.input.main-input {:on-change #(change-handler (-> % .-target .-value))
               :auto-focus true
               :value input-value
               :ref #(reset! input-ref %)
               :tab-index 0}]]
     [suggestion-box input-value]
     [:button {:type "submit"} "submit"]]))

(defn current-page []
  (println @db)
  (let [{:keys [page params]} (session/get :active-route)
        pages {:home-page home-page
               :reference-page reference-page}
        component (pages page)]
    (if component
      [:div [component params]]
      [:div "sorry page not found"])))

(defn app []
  [:div
   [verb-search]
   [current-page]])
