(ns links.auth.core
  (:require [buddy.auth.backends :as backends]
            [compojure.core :refer [defroutes POST]]
            [links.util :as util]
            [ring.util.http-response :as response]
            [honeysql.core :as sql]
            [honeysql.helpers :refer :all :as helpers]
            [links.db.core :as db]
            [buddy.sign.jwt :as jwt]))

(def secret "a-very-secret-string")

(def backend (backends/jws {:secret secret}))

(defn jwt-sign [claims]
  (jwt/sign claims secret {:alg :hs512}))

(defn create-user [user]
  (db/execute (-> (insert-into :users)
                (values [user]))))

(defn get-users [{:keys [username]}]
  (db/query {:select [:id]
             :from [:users]
             :where [:= :users.username username]}))

(defn handle-signup [{:keys [id username] :as user}]
  (if  (empty? (get-users {:username username}))
    (do (create-user user)
        {:msg "User created"
         :token (jwt-sign {:id id})})
    (throw (ex-info "User already exists"
                    {:reason "User already exists"}))))

(defroutes auth-routes
  (POST "/login" {user-info :params}
        (util/send-response (response/ok user-info)))
  (POST "/signup" {{:keys [username password email]} :params}
        (try
          (util/ok-response (handle-signup {:id (util/uuid)
                                            :username username
                                            :pass password}))
          (catch Exception e (util/send-response (response/bad-request
                                                  (ex-data e)))))))
