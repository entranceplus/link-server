(ns links.auth.core-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [honeysql.helpers :refer [delete-from where]]
            [links.db.core :as db]
            [links.handler :refer [app]]
            [links.test.util :as util :refer [get-body POST]]))

(def user {:username "a user"
           :password "sadjasjkd"})

(defn delete-user [user]
  (db/execute (-> (delete-from :users)
                  (where [:= (:username user) :username])))
  (println "user deleted"))

(use-fixtures
  :once (fn [f]
          (println "oohjojn")
          (util/init)
          (delete-user user)
          (f)
          (delete-user user)))

(deftest auth
  (testing "signup"
    (let [response (POST "/signup" user app)
          body (get-body response)]
      (and (is (= 200 (:status response)))
           (is ((complement nil?) (:msg body)))
           (is ((complement nil?) (:token body))))))

  (testing "login"
    (let [response (POST "/login" user app)
          body (get-body response)]
      (and (is (= 200 (:status response)))
           (is ((complement nil?) (:msg body)))
           (is ((complement nil?) (:token body)))))))
