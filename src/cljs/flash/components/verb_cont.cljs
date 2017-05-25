(ns flash.components.verb-cont)

(defn- form-key->subject [form-key]
  ({"form_1s" "yo"
    "form_2s" "tú"
    "form_3s" "él/ella/Ud."
    "form_1p" "nosotros"
    "form_2p" "vosotros"
    "form_3p" "ellos/ellas/Uds."
    } form-key))

(defn- conjugaction-useage-col [verbs]
  [:div
   (for [[k v] verbs]
     [:div {:key (str k v)} (str (form-key->subject k) ": " v)])])

(defn- conjugation-useage [verbs]
  (let [[first-3 last-3] (partition-all 3 verbs)]
    [:div
     [conjugaction-useage-col first-3]
     [conjugaction-useage-col last-3]]))

(defn- conjugation-list [verbs]
  [:div
   (for [[k v] verbs]
     [:div {:key k}
      [:h3 k]
      [conjugation-useage v]])])

(defn verb-cont [useage]
  [:div
   (for [[k v] useage]
     [:div {:key k}
      [:h2 k]
      [conjugation-list v]])])
