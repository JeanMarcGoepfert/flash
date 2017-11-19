(ns flash.components.verb-intro)

(defn verb-intro [v]
  [:div.verb-intro.capitalise
   [:p [:strong "gerund: "] (v "gerund")]
   [:p [:strong "past participle: "] (v "pastparticiple")]])
