(ns links.utility
  (:require [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [environ.core :refer [env]]
            [mount.core :as mount]
            [muuntaja.core :as muuntaja]
            [ring.mock.request :as mock]))

;; ns unalias
;;
;; (ns-aliases *ns*)

 ;;(ns-unalias *ns* 'migrations)

(def m (muuntaja/create))

(defn- generate-string [data]
  (->> data
       (muuntaja/encode m "application/json")
       slurp))

(defn- parse-string [json]
  (muuntaja/decode m "application/json" json))

(defn post-request [url data app]
  (-> (mock/request :post
                    url
                    (generate-string data))
      (mock/content-type "application/json")))

(defn POST! [request app]
  (-> request
      ((app))))

(defn POST [url data app]
  (POST! (post-request url data app) app))

(defn GET [url app]
  (-> (mock/request :get url)
      ;; (mock/content-type "application/json")
      app))



(defn get-body [response]
  (-> response
      :body
      parse-string))


(defn init []
  "start the lifecycle hooks like db and get all environment"
  ;; (migrations/migrate ["migrate"] {:classname "org.postgresql.Driver"
  ;;                                  :subprotocol "postgresql"
  ;;                                  :subname ""
  ;;                                  :user (:dbuser env)
  ;;                                  :name (:dbname env)
  ;;                                  :password (:dbpassword env)})
  )
