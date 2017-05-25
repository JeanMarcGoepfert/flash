(ns flash.components.verb-intro)

(defn verb-intro [v]
  [:div.verb-intro
   [:p [:strong "gerund: "] (v "gerund")]
   [:p [:strong "past participle: "] (v "pastparticiple")]])
