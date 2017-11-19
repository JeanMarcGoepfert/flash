(ns flash.components
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [flash.data :refer [db]]
            [flash.routes :refer [reference]]
            [flash.util :refer [str->dashcase dashcase->str]]
            [flash.components.omni-select :refer [omni-select]]
            [flash.components.verb-heading :refer [verb-heading]]
            [flash.components.verb-intro :refer [verb-intro]]
            [flash.components.verb-content :refer
             [verb-content conjugation-list conjugation-useage]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))

(defn home-page []
  [:div.content-wrapper
   [:p.align-center
    "Conjugate Spanish verbs by typing them into the box above and hitting enter."]
   [:div.todays-content
    [:h2.link
     [:span
     "Todays Verb: "]
     [:a.link {:href "/aprender" :title "Aprender spanish conjugation"} "Aprender - To Learn"]]
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
      [:span "Previous: "]
      [:a.link {:href (str "/" prev) :title (str prev " spanish conjugation")} prev]]
     [:span.next
      [:span "Next: "]
      [:a.link {:href (str "/" next) :title (str next " spanish conjucation")} next]]]))

(defn verb-page [params]
  (let [content (get-in @db [:active-verb "useage"])
        {verb :verb mood :mood} (get-in (session/get :active-route) [:params])
        loading (@db :active-verb-loading)]
    (cond
      loading [:div.loading-spinner]
      (empty? content) [not-found]
      :else [:div
             [title-section]
             [:div.content-wrapper
              [:div.verb-cont
               [verb-content content verb]
               [pagination]]]])))

(defn bread-crumbs [{links :links}]
  [:ul.bread-crumbs
   (for [link (butlast links)]
     (let [{href :href text :text title :title} link]
       [:li.bread-crumb {:key text}
        [:a {:href (link :href) :title title} text]]))
   [:li.bread-crumb.active {:key ((last links) :text)} ((last links) :text)]])


(defn mood-page [params]
  (let [active-verb (@db :active-verb)
        {mood :mood verb :verb} (get-in (session/get :active-route) [:params])
        loading (@db :active-verb-loading)
        content (get-in active-verb ["useage" (dashcase->str mood)])]
    (cond
      loading [:div.loading-spinner]
      (empty? content) [not-found]
      :else [:div
             [title-section]
             [:div.content-wrapper
              [bread-crumbs {:links [{:text verb :href (str "/" verb) :title (str verb " spanish conjugation")}
                                     {:text mood :href (str "/" verb "/" mood)}]}]
              [:div.verb-cont
               [conjugation-list content mood (dashcase->str verb)]
               [pagination]]]])))

(defn tense-page [params]
  (let [active-verb (@db :active-verb)
        {mood :mood tense :tense verb :verb} (get-in (session/get :active-route) [:params])
        loading (@db :active-verb-loading)
        content (get-in active-verb ["useage" (dashcase->str mood) (dashcase->str tense)])]
    (cond
      loading [:div.loading-spinner]
      (empty? active-verb) [not-found]
      :else [:div
             [title-section]
             [:div.content-wrapper
              [bread-crumbs {:links [{:text verb :href (str "/" verb)}
                                     {:text mood :href (str "/" verb "/" mood) :title (str verb " " (dashcase->str mood) " spanish conjugation")}
                                     {:text tense :href (str "/" verb "/" mood "/" tense)}]}]
              [:div.verb-cont
               [conjugation-useage content verb]
               [pagination]
               ]]])))

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
