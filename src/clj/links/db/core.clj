(ns links.db.core
  (:require [clojure.java.jdbc :as jdbc]
            [conman.core :as conman]
            [honeysql.core :as sql]
            [honeysql.helpers :as helpers :refer :all]
            [links.config :refer [env]]
            [links.util :as util]
            [mount.core :refer [defstate]]
            [clojure.spec.alpha :as s]
            [system.repl :refer [system]]))

(defn query [sqlmap]
  (jdbc/query (:db system) (-> sqlmap sql/build sql/format)))

(defn execute! [sqlmap]
  (jdbc/execute! (:db system) (sql/format sqlmap)))

(s/fdef prep-insert-data
        :args (s/cat :data map?)
        :ret vector?
        :fn #(every? :id (-> % :ret)))

(defn- prep-insert-data
  "if you have not set id it will"
  [data]
  (mapv (fn [data]
         (cond-> data
           (nil? (:id data)) (assoc :id (util/uuid))))
       (util/make-vec-if-not data)))

;; (require '[clojure.spec.test.alpha :as stest])
;; (stest/instrument `prep-insert-data)
;; (println (stest/check `prep-insert-data))

(defn add
  "insert data into table"
  [table data]
  (if-let [rows (prep-insert-data data)]
    (when ((complement empty?) rows)
      (execute! (-> (insert-into table)
                    (values rows)))
      rows)))
