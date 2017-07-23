(ns flash.middleware
  (:require
    [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
    [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
    [ring.middleware.gzip :refer [wrap-gzip]]))

(defn wrap-app [handler]
  (-> handler
      (wrap-defaults site-defaults)
      (wrap-gzip)))

(defn wrap-api [handler]
  (-> handler
      wrap-json-response
      (wrap-json-body  {:keywords? true})
      (wrap-defaults api-defaults)))
