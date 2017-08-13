(ns flash.components.verb-content
  (:require [reagent.session :as session]
            [flash.util :refer [str->dashcase dashcase->str]]
            [flash.data :refer [db]]))

(defn- form-key->subject [form-key]
  ({"form_1s" "yo"
    "form_2s" "tú"
    "form_3s" "él/ella/Ud."
    "form_1p" "nosotros"
    "form_2p" "vosotros"
    "form_3p" "ellos/ellas/Uds."
    } form-key))

(defn- conjugaction-useage-col [verbs]
  [:div.verb-col
   (for [[k v] verbs]
     [:div.verb-row
      {:key (str k v)}
      [:strong (str (form-key->subject k) " ")]
      v])])

(defn- conjugation-useage [verbs]
  (let [[first-3 last-3] (partition-all 3 verbs)]
    [:div.verb-cols
     [conjugaction-useage-col first-3]
     [conjugaction-useage-col last-3]]))

(defn- conjugation-list [verbs]
  [:div.tenses
   (for [[k v] verbs]
     [:div {:key k}
      [:h3 k]
      [conjugation-useage v]])])

(defn- mood-list [useage verb]
  [:div
   (for [[k v] useage]
     [:div {:key k}
      [:h2
       [:a {:href (str "/" verb "/" (str->dashcase k))} k]]
      [conjugation-list v]])])

(defn verb-content []
  (let [{verb :verb mood :mood} (get-in (session/get :active-route) [:params])
        {useage "useage"} (@db :active-verb)]
    [:div.verb-cont
     (cond
       (not-empty mood) [mood-list (filter (fn [[k v]] (= k (dashcase->str mood))) useage) verb]
       :else [mood-list useage verb]
       )
     ]))