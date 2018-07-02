(ns user
  (:require [snow.repl :as repl]
            [snow.client :as c]
            [snow.env :refer [profile]]
            [links.systems :as sys]
            [buddy.sign.jwt :as jwt]))

(def auth-url "https://aviana.herokuapp.com")

(def res (c/post (str auth-url "/app/signup")
                 :body {:email (-> (profile) :aviana-email)
                        :password (-> (profile) :aviana-password)}))

(def api-key (-> res :body :api_key))

(def user {:email "useremail@example.com"
           :password "pass"})

(-> (str auth-url "/user/signup")
   (c/post :body user
           :headers {:app_key api-key})
   :body)

(def token (-> (str auth-url "/user/signin")
              (c/post :body user
                      :headers {:app_key api-key})
              :body
              :token))

(jwt/unsign token "secret")

(repl/start! sys/system-config)

(repl/stop!)
