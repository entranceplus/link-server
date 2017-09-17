(ns links.db.core
  (:require [clj-time.jdbc]
            [clojure.java.jdbc :as jdbc]
            [conman.core :as conman]
            [honeysql.core :as sql]
            [links.config :refer [env]]
            [honeysql.core :as sql]
            [honeysql.helpers :refer :all :as helpers]
            [mount.core :refer [defstate]])
  (:import [java.sql
            BatchUpdateException
            PreparedStatement]))

(defstate ^:dynamic *db*
           :start (conman/connect! {:jdbc-url (env :database-url)})
           :stop (conman/disconnect! *db*))

(defn query [sqlmap]
  (jdbc/query *db* (sql/format sqlmap)))

(defn execute [sqlmap]
  (jdbc/execute! *db* (sql/format sqlmap)))
