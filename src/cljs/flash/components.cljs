(ns flash.components
    (:require [reagent.core :as reagent :refer [atom]]
              [clojure.string :refer [starts-with? replace-first]]
              [reagent.session :as session]
              [flash.routes :refer [reference]]
              [flash.omni-select :refer [omni-select]]
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


(def db
  (reagent/atom {:verb-input ""
                 :suggestions []
                 :verb-input-component nil
                 :option-tab-index 0}))

(defn get-active-suggestion [db]
  (nth (db :suggestions) (db :option-tab-index) ""))

(defn filter-verbs [input-value]
  (if (empty? input-value)
    '()
    (filter #(starts-with? % input-value) (keys verbs))))

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

(defn submit-handler [e]
  (let [suggestion (get-active-suggestion @db)
        value (@db :verb-input)
        path (if (empty? suggestion)
               value
               suggestion)]
    (do
      (.preventDefault e)
      (if-not (empty? value)
        (do
          (accountant/navigate! (reference {:verb path}))
          (swap! db assoc :verb-input "")
          (swap! db assoc :suggestions [])
          (swap! db assoc :option-tab-index 0))))))

(defn click-handler [e verb]
  (do
    (accountant/navigate! (reference {:verb verb}))
    (swap! db assoc :verb-input "")
    (swap! db assoc :suggestions [])
    (swap! db assoc :option-tab-index 0)))

(defn key-down-handler [e]
  (let [tab-keycode 9
        pressed-keycode (.-keyCode e)
        shift-key-held? (.-shiftKey e)
        tab-pressed? (= tab-keycode pressed-keycode)
        inc-or-dec (if shift-key-held? dec inc)
        has-input? (not (empty? (@db :verb-input)))]
    (if (and tab-pressed? has-input?)
      (let [current-tab-index (@db :option-tab-index)]
        (do
          (.preventDefault e)
          (swap! db
                 assoc :option-tab-index
                 (mod (inc-or-dec current-tab-index)
                      (count (@db :suggestions)))))))))

(defn change-handler [value]
  (do
    (swap! db assoc :verb-input value)
    (swap! db assoc :suggestions (filter-verbs value))
    (swap! db assoc :option-tab-index 0)))

(defn suggestion-box [input-value]
  (if (not (empty? input-value))
    (let [matches (@db :suggestions)
          tab-index (@db :option-tab-index)]
      [:div.suggestion-box-wrapper
       [:ul.tab-options
        (map-indexed
          (fn [index verb]
            [:li.tab-option {:key verb
                             :on-click #(click-handler % verb)
                             :class-name (if (= index tab-index) "active-tab-option")}
             verb])
          matches)]
       ]
      )))

(defn verb-search []
  (let [input-value (@db :verb-input)
        input-suggestion (get-active-suggestion @db)]
    [:form.verb-search {:on-submit #(submit-handler %)}
     [:div.input-wrapper
      [:span.suggestion-wrapper
       [:span.suggestion-text.common-suggestion input-value]
       [:input.suggestion-text.remaining-suggestion
        {:value (replace-first input-suggestion (re-pattern input-value) "")
         :read-only true
         }]]
      [:input.input.main-input
       {:on-key-down #(key-down-handler %)
        :on-change #(change-handler (-> % .-target .-value))
        :value input-value
        :tab-index 0}]]
     [suggestion-box input-value]
     [:button {:type "submit"} "submit"]]))

(defn current-page []
  (let [{:keys [page params]} (session/get :active-route)
        pages {:home-page home-page
               :reference-page reference-page}
        component (pages page)]
    (if component
      [:div [component params]]
      [:div "sorry page not found"])))



(defn app []
  [:div
   ;[omni-select verbs submit-hander]
   [verb-search]
   [current-page]])
