(ns links.domain-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [honeysql.helpers :refer [delete-from where]]
            [links.auth.core-test :refer [auth-user]]
            [links.db.core :as db]
            [links.handler :refer [app]]
            [links.utility :as util]
            [clj-http.client :as client]
            [ring.mock.request :as mock]))

(def user {:username "auser"
           :password "sadjasjkd"})

(def token (atom ""))

(defn delete-user [user]
  ;; (db/execute! (-> (delete-from :users)
  ;;                 (where [:= (:username user) :username])))
  (println "user deleted"))

(use-fixtures
  :once (fn [f]
          (println "oohjojn")
          (util/init)
          (reset! token (-> user
                            auth-user
                            util/get-body
                            :access_token))
          (f)
          (delete-user user)))

(def link {:id "asdasd"
           :url "http://dsfdfdsfs.ascom"
           :tags ["asd" "sadas" "aasdasdsd" "asdasd"]})

(deftest link-test
  (testing "Links should be saved"
    (is (= 200 (:status (client/post "https://entranceplus.in/links"
                                     {:form-params link
                                      :content-type :json
                                      :format :json
                                      :redirect-strategy :lax
                                      :headers {"Authorization" (str "Bearer " @token)}
                                      }))))))
