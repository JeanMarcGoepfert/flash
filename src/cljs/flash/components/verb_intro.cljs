(ns flash.components.verb-intro)

(defn verb-intro [v]
  [:div
   [:div (str "Gerund: " (v "gerund"))]
   [:div (str "Past Participle: " (v "pastparticiple"))]])
