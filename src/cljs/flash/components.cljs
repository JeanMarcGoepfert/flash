(ns flash.components
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [flash.data :refer [db]]
            [flash.routes :refer [reference]]
            [flash.components.omni-select :refer [omni-select]]
            [flash.components.verb-heading :refer [verb-heading]]
            [flash.components.verb-intro :refer [verb-intro]]
            [flash.components.verb-content :refer [verb-content]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))

(defn home-page []
  [:div.content-wrapper
   [:p.align-center "Conjugate any Spanish verb. Simply type in the box above and hit enter"]])

(defn not-found
  ([]
   [:div.not-found
    [:h1 "Sorry, this page not found."]
    [:a {:href "/"} "Go home"]])
  ([custom-message]
   [:div.not-found
    [:h1 custom-message]
    [:a {:href "/"} "Go home"]]))

(defn reference-content [verb-meta verb-useage]
  [:div
   [:div.title-section
    [:div.content-wrapper
     [verb-heading (verb-meta "infinitive_english") (verb-meta "infinitive")]
     [verb-intro verb-meta]]]
   [:div.content-wrapper
    [verb-content verb-useage]]])

(defn reference-page [params]
  (let [verb (get-in (session/get :active-route) [:params :verb])
        {verb-useage "useage" verb-meta "meta"} (@db :active-verb)
        loading (@db :active-verb-loading)
        not-found-msg (str "Sorry, we couldn't find the verb: " verb)]
    (cond
      loading [:div.loading-spinner]
      (nil? verb-meta) [not-found not-found-msg]
      :else [reference-content verb-meta verb-useage])))

(defn current-page []
  (let [{:keys [page params]} (session/get :active-route)
        pages {:home-page home-page
               :reference-page reference-page}
        component (pages page)]
    (if component
      [:div [component params]]
      [:div [not-found]])))

(defn on-change [value]
  (accountant/navigate! (reference {:verb value})))

(defn app []
  [:div.page-wrapper
   [:div
    [:div.input-section
     [:div.content-wrapper
      [omni-select (@db :verb-list) on-change]]]]
   [current-page]])
