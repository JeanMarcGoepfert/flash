(ns flash.components
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :refer [starts-with? replace-first]]
            [reagent.session :as session]
            [flash.data :refer [db]]
            [flash.routes :refer [reference]]
            [flash.components.omni-select :refer [omni-select]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))

(defn home-page [])

(defn form-key->subject [form-key]
  ({"form_1s" "yo"
    "form_2s" "tú"
    "form_3s" "él/ella/Ud."
    "form_1p" "nosotros"
    "form_2p" "vosotros"
    "form_3p" "ellos/ellas/Uds."
    } form-key)
  )

(defn verb-intro [v]
  [:div
   [:div (str "Gerund: " (v "gerund"))]
   [:div (str "Past Participle: " (v "pastparticiple"))]])

(defn verb-heading [infinitive-eng infinitive-span]
  [:h2 (str infinitive-span " - " infinitive-eng)])

(defn conjugaction-useage-col [verbs]
  [:div
   (for [[k v] verbs]
     [:div {:key (str k v)} (str (form-key->subject k) ": " v)])])

(defn conjugation-useage [verbs]
  (let [[first-3 last-3] (partition-all 3 verbs)]
    [:div
     [conjugaction-useage-col first-3]
     [conjugaction-useage-col last-3]]))

(defn conjugation-list [verbs]
  (for [[k v] verbs]
    [:div {:key k}
     [:h3 k]
     [conjugation-useage v]]))

(defn verb-cont [useage]
  (for [[k v] useage]
    [:div {:key k}
     [:h2 k]
     (conjugation-list v)]))

(defn reference-page []
  (let [verb (get-in (session/get :active-route) [:params :verb])
        {verb-useage "useage" verb-meta "meta"} (@db :active-verb)
        loading (@db :active-verb-loading)]
    (if-not loading
      [:div
       [verb-heading (verb-meta "infinitive_english") (verb-meta "infinitive")]
       [:div
        (verb-intro verb-meta)
        [:hr ]
        (verb-cont verb-useage)]])
    ))

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
