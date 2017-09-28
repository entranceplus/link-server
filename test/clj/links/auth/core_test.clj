(ns links.auth.core-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [honeysql.helpers :refer [delete-from where]]
            [links.db.core :as db]
            [links.handler :refer [app]]
            [links.utility :as util]))

(def user {:username "a user"
           :password "sadjasjkd"})

(defn delete-user [user]
  (db/execute! (-> (delete-from :users)
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
  (testing "auth"
    (let [response (util/POST "/auth" user app)
          body (util/get-body response)]
      (and (is (= 200 (:status response)))
           (is (= "User created" (:msg body)))
           (is ((complement nil?) (:token body))))))

  (testing "auth with existing user"
    (let [response (util/POST "/auth" user app)
          body (util/get-body response)]
      (and (is (= 200 (:status response)))
           (is (= "User logged in"  (:msg body)))
           (is ((complement nil?) (:token body)))))))
