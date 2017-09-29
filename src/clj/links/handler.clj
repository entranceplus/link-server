(ns links.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [compojure.route :as route]
            [links.auth.core :as auth]
            [links.env :refer [defaults]]
            [links.layout :refer [error-page]]
            [links.middleware :as middleware]
            [links.routes.home :refer [home-routes]]
            [links.domain :as domain]
            [mount.core :as mount]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (-> #'auth/auth-routes
        (wrap-routes middleware/wrap-formats))
    (-> #'domain/link-routes
        (wrap-routes middleware/wrap-formats))
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
