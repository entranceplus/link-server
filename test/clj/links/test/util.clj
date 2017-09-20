(ns links.test.util
  (:require [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [links.config :refer [env]]
            [luminus-migrations.core :as migrations]
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

(defn POST [url data app]
  (-> (mock/request :post
                    url
                    (generate-string data))
      (mock/content-type "application/json")
      ((app))))

(defn GET [url app]
  (-> (mock/request :get url)
      (mock/content-type "application/json")
      ((app))))

(defn get-body [response]
  (-> response
      :body
      slurp
      parse-string))


(defn init []
  "start the lifecycle hooks like db and get all environment"
  (mount/start
      #'links.config/env
      #'links.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url])))
