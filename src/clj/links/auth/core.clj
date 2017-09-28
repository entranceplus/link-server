(ns links.auth.core
  (:require [ajax.core :as req :refer [raw-response-format]]
            [buddy.auth.backends :as backends]
            [buddy.sign.jwt :as jwt]
            [compojure.core :refer [defroutes POST]]
            [crypto.password.pbkdf2 :as password]
            [honeysql.helpers :as helpers :refer :all]
            [links.db.core :as db]
            [links.util :as util]
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

(defn get-token [{:keys [id username password]}]
  (req/POST "http://entranceplus.in:8001/oauth2/token"
            {:params {:client_id "JOERouFGerPXCvAtCOWvdg1DIhzRhUum"
                      :client_secret "T94S0O6RII3dmfpXA5MYRjOeBOIrsWOY"
                      :grant_type "password"
                      :scope "email"
                      :provision_key "function"
                      :authenticated_userid id
                      :username username
                      :password password}
             :response-format :json
             :keywords? true}))


(defn handle-auth [{:keys [username password] :as user}]
  (if-let  [users (seq (get-users {:username username}))]
    (when-let [{:keys [pass id]} (first (get-users {:username username}))]
      (when (password/check password pass)
        {:msg "User logged in"
         :token (jwt-sign {:id id})}))
    (let [id  (util/uuid)]
      (create-user {:id id
                    :username username
                    :pass (password/encrypt password)})
      {:msg "User created"
       :token (jwt-sign {:id id})})))

;; resource owner password credentials
(defroutes auth-routes
  (POST "/auth" {user-info :params}
        (if-let [login-response (handle-auth user-info)]
          (util/ok-response login-response)
          (util/send-response (response/bad-request
                                 {:reason "Incorrect credentials"}))))
  (POST "/signup" {{:keys [username password]} :params}
        (try
          (util/ok-response (handle-signup {:username username
                                            :password password}))
          (catch Exception e (util/send-response (response/bad-request
                                                  (ex-data e)))))))
