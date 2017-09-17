(ns links.db-test
  (:require [links.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [links.config :refer [env]]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'links.config/env
      #'links.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

;; (deftest test-users
;;   (jdbc/with-db-transaction [t-conn *db*]
;;     (jdbc/db-set-rollback-only! t-conn)
;;     (is (= 1 (db/create-user!
;;                t-conn
;;                {:id         "1"
;;                 :first_name "Sam"
;;                 :last_name  "Smith"
;;                 :username   "a unsername"
;;                 :email      "sam.smith@example.com"
;;                 :pass       "pass"})))
;;     (is (= {:id         "1"
;;             :first_name "Sam"
;;             :last_name  "Smith"
;;             :email      "sam.smith@example.com"
;;             :pass       "pass"
;;             :admin      nil
;;             :username   "a unsername"
;;             :last_login nil
;;             :is_active  nil}
;;            (db/get-user t-conn {:id "1"})))))
