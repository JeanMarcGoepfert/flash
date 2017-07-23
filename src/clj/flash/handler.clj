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
     (include-js "/js/app.js")]))

(def verbs
  (json/parse-string (slurp "resources/verbs.json")))

(def verb-lists
  (into {} (map (fn [[k v]] [k (v :meta :verb_english)]) verbs)))

(defn verb-list []
  {:body verb-lists})

(defn get-verb [{params :params}]
  (let [verb (params :verb)]
    (if verb
      {:body (verbs (params :verb))}
      {:status 404 :message "Verb not found"}
      ))

  {:body (verbs (params :verb))})

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
