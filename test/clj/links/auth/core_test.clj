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

(defn auth-user [user]
  (util/POST "/auth" user app))

(deftest auth
  (testing "auth"
    (let [response (auth-user user)
          body (util/get-body response)]
      (and (is (= 200 (:status response)))
           (is ((complement nil?) (:access_token body)))
           (is ((complement nil?) (:refresh_token body))))))

  (testing "auth with existing user"
    (let [response (auth-user user)
          body (util/get-body response)]
      (and (is (= 200 (:status response)))
           (is ((complement nil?) (:access_token body)))
           (is ((complement nil?) (:refresh_token body)))))))
