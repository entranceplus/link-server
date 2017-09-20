(ns links.util
  (:require [clojure.spec.alpha :as s]
            [ring.util.http-response :as response]))

(defn send-response [response]
  (-> response
      (response/header "Content-Type" "application/json; charset=utf-8")))

(defn send-err-response [response]
  (-> response
      (response/header "Content-Type" "application/json; charset=utf-8")))

(defn ok-response [response]
  (-> (response/ok response)
      send-response))

(defn send-todo []
  (send-response (response/ok {:msg "Not implemented"})))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn make-vec-if-not [maybe-vec]
  (if ((complement seq?) maybe-vec)
    (conj []  maybe-vec)
    (vec maybe-vec)))
