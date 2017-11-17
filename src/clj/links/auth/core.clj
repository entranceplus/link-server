(ns links.auth.core
  (:require [buddy.auth.backends :as backends]
            [buddy.sign.jwt :as jwt]
            [compojure.core :refer [defroutes POST]]
            [crypto.password.pbkdf2 :as password]
            [honeysql.helpers :as helpers :refer :all]
            [links.db.core :as db]
            [links.util :as util]
            [clj-http.client :as client]
            [ring.util.http-response :as response]))

(def secret "a-very-secret-string")

(def backend (backends/jws {:secret secret}))

(defn jwt-sign [claims]
  (jwt/sign claims secret {:alg :hs512}))

(defn create-user [user]
  (db/execute! (-> (insert-into :users)
                   (values [user]))))

(defn get-users [{:keys [username]}]
  (db/query {:select [:id :pass]
             :from [:users]
             :where [:= :users.username username]}))

(def oauth-config {:client_id "JOERouFGerPXCvAtCOWvdg1DIhzRhUum"
                   :client_secret "T94S0O6RII3dmfpXA5MYRjOeBOIrsWOY"
                   :grant_type "password"
                   :scope "username"
                   :provision_key "function"})

(defn get-token
  "get token from kong"
  [{:keys [id username password]}]
  (-> "https://links.entranceplus.in/oauth2/token"
      (client/post {:form-params (merge oauth-config
                                        {:authenticated_userid id
                                         :username username
                                         :password password})
                    :as :json
                    :content-type :json})
      :body))

(defn ensure-user
  "if username and password combo is present then get user,
  if not then create user"
  [{:keys [username password] :as user}]
  (if-let  [{:keys [pass id]} (first (seq (get-users {:username username})))]
    (when (password/check password pass)
        (merge user {:id id}))
    (let [id  (util/uuid)
          user {:id id
                :username username
                :pass (password/encrypt password)}]
      (create-user user)
      user)))

(defn handle-auth
  "issue token for this user"
  [user]
  (some-> user ensure-user get-token))

;; resource owner password credentials
(defroutes auth-routes
  (POST "/auth" {user-info :body}
        (if-let [login-response (handle-auth user-info)]
          (util/ok-response login-response)
          (util/send-response (response/bad-request
                               {:reason "Incorrect credentials"})))))
