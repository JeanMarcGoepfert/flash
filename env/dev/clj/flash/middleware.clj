(ns flash.middleware
  (:require
    [ring.middleware.json :refer  [wrap-json-response wrap-json-body]]
    [ring.middleware.defaults :refer  [wrap-defaults api-defaults site-defaults]]
    [prone.middleware :refer [wrap-exceptions]]
    [ring.middleware.reload :refer [wrap-reload]]))

(defn wrap-app [handler]
  (-> handler
      (wrap-defaults site-defaults)
      wrap-exceptions
      wrap-reload))

(defn wrap-api [handler]
  (-> handler
      wrap-json-response
      (wrap-json-body  {:keywords? true})
      (wrap-defaults api-defaults)
      wrap-exceptions
      wrap-reload))
