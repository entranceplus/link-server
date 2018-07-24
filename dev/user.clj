(ns user
  (:require [snow.repl :as repl]
            [snow.client :as c]
            [snow.env :refer [profile]]
            [links.systems :as sys]
            [buddy.sign.jwt :as jwt]
            [shadow.cljs.devtools.server :as server]
            [shadow.cljs.devtools.api :as shadow]))

(def auth-url "https://aviana.herokuapp.com")

(def res (c/post (str auth-url "/app/signup")
                 :body {:email (-> (profile) :aviana-email)
                        :password (-> (profile) :aviana-password)}))

(def api-key (-> res :body :api_key))

(def user {:email "useremail@example.com"
           :password "pass"})

#_(-> (str auth-url "/user/signup")
      (c/post :body user
              :headers {:app_key api-key})
      :body)

#_(def token (-> (str auth-url "/user/signin")
                 (c/post :body user
                         :headers {:app_key api-key})
                 :body
                 :token))

#_(jwt/unsign token "secret")


#_(repl/start! sys/system-config)
#_(repl/stop!)

(defn -main [& args]
  (println "Starting nrepl")
  (repl/start-nrepl)
  (println "Starting clj systems")
  (repl/start! sys/system-config)
  (server/start!)
  (shadow/dev :app))
