(ns links.middleware
  (:require [clojure.tools.logging :as log]
            [immutant.web.middleware :refer [wrap-session]]
            [links.util :as util]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.flash :refer [wrap-flash]]
            [ring.util.http-response :as response])
  (:use  [ring.middleware.json]
         [ring.middleware
          params
          keyword-params
          nested-params]))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error t)
        (util/send-err-response
         (response/internal-server-error  {:status 500
                                           :title (.getMessage t)
                                           :message "We've dispatched a team of highly trained gnomes to take care of the problem."}))))))

(defn wrap-formats [handler]
  (-> handler
      (wrap-keyword-params)
      (wrap-nested-params)
      (wrap-params)
      (wrap-json)))

(defn wrap-base [handler]
  (-> handler
      wrap-flash
      (wrap-session {:cookie-attrs {:http-only true}})
      wrap-internal-error
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:static :resources] "")
            (assoc-in [:security :anti-forgery] false)
            (dissoc :session)))
      ))
