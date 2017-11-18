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

(defn- conjugation-list [mood-values mood verb]
  [:div.tenses
   (for [[k v] mood-values]
     [:div {:key k}
      [:h3 [:a {:href (str "/" verb "/" (str->dashcase mood) "/" (str->dashcase k))} k]]
      [conjugation-useage v]])])

(defn- verb-content [useage verb]
  [:div.verb-cont
   (for [[mood mood-values] useage]
     [:div {:key mood}
      [:h2
       [:a {:href (str "/" verb "/" (str->dashcase mood))} mood]]
      [conjugation-list mood-values mood verb]])])
