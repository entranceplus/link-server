(ns links.auth.core-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [honeysql.helpers :refer [delete-from where]]
            [links.db.core :as db]
            [clj-http.client :as client]
            [links.handler :refer [app]]
            [links.utility :as util]))

(def user {:username "a user"
           :password "sadjasjkda"})

(defn delete-user [user]
  (db/execute! (-> (delete-from :users)
                   (where [:= (:username user) :username])))
  (println "user deleted"))

(use-fixtures
  :once (fn [f]
          (util/init)
          ;; (delete-user user)
          (f)
          ;; (delete-user user)
          ))

(defn auth-user [user]
  (client/post "http://localhost:9000/auth"
               {:body user
                :content-type :json
                :redirect-strategy :lax
                :format :json}
               ))

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
