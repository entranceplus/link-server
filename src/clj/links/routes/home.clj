(ns links.routes.home
  (:require [links.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            ;; [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [ring.util.response :refer [response content-type resource-response]]))

;; (defn home-page []
;;   (layout/render "home.html"))

(defroutes home-routes
  (GET "/" []
       (-> (resource-response "home.html")
           (content-type "text/html"))))
