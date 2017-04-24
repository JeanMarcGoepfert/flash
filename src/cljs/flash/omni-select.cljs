(ns flash.omni-select
    (:require [reagent.core :as reagent :refer [atom]]
              [clojure.string :refer [starts-with? replace-first]]
              [reagent.session :as session]
              [flash.routes :refer [reference]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(def db
  (reagent/atom {:verb-input ""
                 :suggestions []
                 :verb-input-component nil
                 :option-tab-index 0}))

(defn get-active-suggestion [db]
  (nth (db :suggestions) (db :option-tab-index) ""))

(defn filter-verbs [verbs input-value]
  (if (empty? input-value)
    '()
    (filter #(starts-with? % input-value) (keys verbs))))

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

(defn change-handler [value verbs]
  (do
    (swap! db assoc :verb-input value)
    (swap! db assoc :suggestions (filter-verbs verbs value))
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

(defn omni-select [verbs]
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
        :on-change #(change-handler (-> % .-target .-value) verbs)
        :value input-value
        :tab-index 0}]]
     [suggestion-box input-value]
     [:button {:type "submit"} "submit"]]))

