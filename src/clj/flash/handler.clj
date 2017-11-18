(ns flash.handler
  (:require [compojure.core :refer [GET defroutes context]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [flash.middleware :refer [wrap-app wrap-api]]
            [cheshire.core :as json]
            [config.core :refer [env]]))

(def mount-target
  [:div#app
   [:div.loading-spinner]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (if (env :prod) (slurp "resources/public/js/analytics.js"))
     (include-js "/js/app.js")]))

(def verbs
  (json/parse-string (slurp "resources/verbs.json")))

(def verb-lists
  (into {} (map (fn [[k v]] [k (get-in v ["meta" "verb_english"])]) verbs)))

(def verb-vector
  (keys verb-lists))

(defn verb-list []
  {:body verb-lists})

(defn getNext [verb]
  (let [index (.indexOf verb-vector verb)]
    (nth verb-vector (+ index 1) (first verb-vector))))

(defn getPrev [verb]
  (let [index (.indexOf verb-vector verb)]
    (println index)
    (nth verb-vector (- index 1) (last verb-vector))))

(defn get-verb [{params :params}]
  (let [verb (verbs (params :verb))]
    (if verb
      {:body {:verb verb :prev (getPrev (params :verb)) :next (getNext (params :verb))}}
      {:status 404 :body {:message "Verb not found"}})))

(defroutes api-routes
  (context
    "/api" []
    (GET "/verb-list" _ (verb-list))
    (GET "/verb/:verb" req (get-verb req)))
  )

(defroutes client-routes
  (resources "/")
  (GET "*" [] (loading-page)))


(defroutes routes
  (wrap-api api-routes)
  (wrap-app client-routes))

(def app routes)
