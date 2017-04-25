(ns flash.components.omni-select
    (:require [reagent.core :as reagent :refer [atom]]
              [clojure.string :refer [starts-with? replace-first]]))

(def db-defaults {:verb-input ""
                  :suggestions []
                  :option-tab-index 0})

(def db (reagent/atom db-defaults))

(defn get-active-suggestion [db]
  (nth (db :suggestions) (db :option-tab-index) ""))

(defn reset-input-state []
  (reset! db db-defaults))

(defn filter-verbs [verbs input-value]
  (if (empty? input-value)
    '()
    (filter #(starts-with? % input-value) (keys verbs))))

(defn submit-handler [e on-change]
  (let [suggestion (get-active-suggestion @db)
        value (@db :verb-input)
        path (if (empty? suggestion) value suggestion)]
    (.preventDefault e)
    (if-not (empty? value)
      (do
        (reset-input-state)
        (on-change path)))))

(defn click-handler [e verb on-change]
  (reset-input-state)
  (on-change verb))

(defn key-down-handler [e]
  (let [tab-keycode 9
        pressed-keycode (.-keyCode e)
        shift-key-held? (.-shiftKey e)
        tab-pressed? (= tab-keycode pressed-keycode)
        inc-or-dec (if shift-key-held? dec inc)
        has-input? (not (empty? (@db :verb-input)))]
    (if (and tab-pressed? has-input?)
      (let [current-tab-index (@db :option-tab-index)]
        (.preventDefault e)
        (swap! db
               assoc :option-tab-index
               (mod (inc-or-dec current-tab-index)
                    (count (@db :suggestions))))))))

(defn change-handler [value verbs]
  (let [new-state {:verb-input value
                   :suggestions (filter-verbs verbs value)
                   :option-tab-index 0}]
    (reset! db new-state)))

(defn tab-suggestions [input-value on-change]
  (if (not (empty? input-value))
    (let [matches (@db :suggestions)
          tab-index (@db :option-tab-index)]
      [:div.tab-suggestions-wrapper
       [:ul.tab-suggestions
        (map-indexed
          (fn [index verb]
            [:li.tab-suggestion
             {:key verb
              :on-click #(click-handler % verb on-change)
              :class-name (if (= index tab-index) "active")}
             verb])
          matches)]])))

(defn suggestion-text [input-suggestion input-value]
  [:span.input-suggestion-wrapper
   [:span.input-suggestion-text.common input-value]
   [:input.input-suggestion-text.remaining
    {:value (replace-first input-suggestion (re-pattern input-value) "")
     :read-only true}]])

(defn input [verbs input-value]
  [:input.input
   {:on-key-down #(key-down-handler %)
    :on-change #(change-handler (-> % .-target .-value) verbs)
    :value input-value
    :tab-index 0}])

(defn omni-select [verbs on-change]
  (let [input-value (@db :verb-input)
        input-suggestion (get-active-suggestion @db)]
    [:form.omni-select {:on-submit #(submit-handler % on-change)}
     [:div.input-wrapper
      [suggestion-text input-suggestion input-value]
      [input verbs input-value]]
     [tab-suggestions input-value on-change]
     [:button.button {:type "submit"}]]))
