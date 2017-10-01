(ns links.middleware
  (:require [clojure.tools.logging :as log]
            [cognitect.transit :as transit]
            [immutant.web.middleware :refer [wrap-session]]
            [links.auth.core :as auth]
            ;; [links.config :refer [env]]
            ;; [links.env :refer [defaults]]
            [muuntaja.core :as muuntaja]
            [muuntaja.format.transit :as transit-format]
            [muuntaja.middleware :refer [wrap-format wrap-params]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.flash :refer [wrap-flash]]
            [buddy.auth.middleware :refer (wrap-authentication)]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [links.util :as util]
            [ring.util.http-response :as response])
  (:import javax.servlet.ServletContext
           org.joda.time.ReadableInstant))

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

(def joda-time-writer
  (transit/write-handler
    (constantly "m")
    (fn [v] (-> ^ReadableInstant v .getMillis))
    (fn [v] (-> ^ReadableInstant v .getMillis .toString))))

(def restful-format-options
  (update
    muuntaja/default-options
    :formats
    merge
    {"application/transit+json"
     {:decoder [(partial transit-format/make-transit-decoder :json)]
      :encoder [#(transit-format/make-transit-encoder
                   :json
                   (merge
                     %
                     {:handlers {org.joda.time.DateTime joda-time-writer}}))]}}))

(defn wrap-formats [handler]
  (let [wrapped (-> handler
                    wrap-params
                    (wrap-format restful-format-options))]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

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
