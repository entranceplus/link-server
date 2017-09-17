(ns links.auth.core
  (:require [buddy.auth.backends :as backends]
            [compojure.core :refer [defroutes POST]]
            [links.util :as util]
            [ring.util.http-response :as response]
            [honeysql.core :as sql]
            [honeysql.helpers :refer :all :as helpers]
            [links.db.core :as db]
            [crypto.password.pbkdf2 :as password]
            [buddy.sign.jwt :as jwt]))

(def secret "a-very-secret-string")

(def backend (backends/jws {:secret secret}))

(defn jwt-sign [claims]
  (jwt/sign claims secret {:alg :hs512}))

(defn create-user [user]
  (db/execute (-> (insert-into :users)
                (values [user]))))

(defn get-users [{:keys [username]}]
  (db/query {:select [:id :pass]
             :from [:users]
             :where [:= :users.username username]}))

(defn handle-signup [{:keys [username password] :as user}]
  (if  (empty? (get-users {:username username}))
    (let [id  (util/uuid)]
      (create-user {:id id
                    :username username
                    :pass (password/encrypt password)})
        {:msg "User created"
         :token (jwt-sign {:id id})})
    (throw (ex-info "User already exists"
                    {:reason "User already exists"}))))

(defn handle-login [{:keys [username password] :as user}]
  (when-let [{:keys [pass id]} (first (get-users {:username username}))]
    (when (password/check password pass)
      {:msg "User logged in"
       :token (jwt-sign {:id id})})))

(defroutes auth-routes
  (POST "/login" {user-info :params}
        (if-let [login-response (handle-login user-info)]
          (util/ok-response login-response)
          (util/send-response (response/bad-request
                                 {:reason "Incorrect credentials"}))))
  (POST "/signup" {{:keys [username password]} :params}
        (try
          (util/ok-response (handle-signup {:username username
                                            :password password}))
          (catch Exception e (util/send-response (response/bad-request
                                                  (ex-data e)))))))
