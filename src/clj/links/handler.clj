(ns links.handler
  (:require [clj-http.client :as client]
            [compojure.core :refer [routes wrap-routes]]
            [compojure.route :as route]
            [links.auth.core :as auth]
            [links.domain :as domain]
            [links.middleware :as middleware]
            [links.routes.home :refer [home-routes]]
            [ring.middleware.http-response :refer [wrap-http-response]]))

(def app-routes
  (routes
    (-> #'home-routes)
    (-> #'auth/auth-routes
        (wrap-routes middleware/wrap-formats))
    (-> #'domain/link-routes
        (wrap-routes middleware/wrap-formats))
    (route/not-found
      (:body
       {:status 404
        :title "page not found"}))
    ))


(def app (middleware/wrap-base #'app-routes))
