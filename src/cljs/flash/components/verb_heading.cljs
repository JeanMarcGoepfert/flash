(ns flash.components.verb-heading)

(defn verb-heading [infinitive-eng infinitive-span]
  [:h1.capitalise [:a {:href (str "/" infinitive-span)} infinitive-span] (str " - " infinitive-eng)])

