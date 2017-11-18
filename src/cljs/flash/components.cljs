(ns flash.components
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [flash.data :refer [db]]
            [flash.routes :refer [reference]]
            [flash.util :refer [str->dashcase dashcase->str]]
            [flash.components.omni-select :refer [omni-select]]
            [flash.components.verb-heading :refer [verb-heading]]
            [flash.components.verb-intro :refer [verb-intro]]
            [flash.components.verb-content :refer [verb-content]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))

(defn home-page []
  [:div.content-wrapper
   [:p.align-center
    "Conjugate Spanish verbs by typing them into the box above."]
   [:div.todays-content
    [:h2.link
     [:span
     "Todays Verb: "]
     [:a.link {:href "/aprender"} "Aprender - To Learn"]]
    ]])

(defn not-found []
  [:div.not-found
   [:h1 "Sorry, this page could not found."]
   [:a {:href "/"} "Go home"]])

(defn title-section []
  (let [verb (get-in (session/get :active-route) [:params :verb])
        {verb-meta "meta"} (@db :active-verb)
        loading (@db :active-verb-loading)
        not-found-msg (str "Sorry, we couldn't find the verb: " verb)]
    [:div.title-section
     [:div.content-wrapper
      [verb-heading (verb-meta "infinitive_english") (verb-meta "infinitive")]
      [verb-intro verb-meta]]]))

(defn pagination []
  (let [prev (@db :prev)
        next (@db :next)]
    [:div.pagination
     [:span.prev
      [:span "Previous Verb: "]
      [:a.link {:href (str "/" prev)} prev]]
     [:span.next
      [:span "Next Verb: "]
      [:a.link {:href (str "/" next)} next]]]))

(defn reference-content [content verb]
  [:div
   [title-section]
   [:div.content-wrapper
    [pagination]
    [verb-content content verb]
    [pagination]]])

(defn verb-page [params]
  (let [content (get-in @db [:active-verb "useage"])
        {verb :verb} (get-in (session/get :active-route) [:params])
        loading (@db :active-verb-loading)]
    (cond
      loading [:div.loading-spinner]
      (empty? content) [not-found]
      :else [reference-content content (dashcase->str verb)])))

(defn mood-page [params]
  (let [active-verb (@db :active-verb)
        {mood :mood verb :verb} (get-in (session/get :active-route) [:params])
        loading (@db :active-verb-loading)
        content {mood (get-in active-verb ["useage" (dashcase->str mood)])}]
    (cond
      loading [:div.loading-spinner]
      (empty? content) [not-found]
      :else [reference-content content (dashcase->str verb)])))

(defn tense-page [params]
  (let [active-verb (@db :active-verb)
        {mood :mood tense :tense verb :verb} (get-in (session/get :active-route) [:params])
        loading (@db :active-verb-loading)
        content {mood {tense (get-in active-verb ["useage" (dashcase->str mood) (dashcase->str tense)])}}]
    (cond
      loading [:div.loading-spinner]
      (empty? active-verb) [not-found]
      :else [reference-content content (dashcase->str verb)])))

(defn current-page []
  (let [{:keys [page params]} (session/get :active-route)
        pages {:home-page home-page
               :reference-page verb-page
               :mood-page mood-page
               :tense-page tense-page}
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
