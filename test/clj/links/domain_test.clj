(ns links.domain-test
  (:require [snow.client :as c]
            [snow.env :refer [profile]]
            [clojure.test :refer [deftest is testing use-fixtures]]))

#_(use-fixtures
    :once (fn [f]
            (util/init)
            (reset! token (-> user
                              auth-user
                              util/get-body
                              :access_token))
            (f)))

(def link {:id "asdasd"
           :url "http://ep.in"
           :user-id "abcd"
           :tags ["asd" "sadas" "aasdasdsd" "asdasd"]})

(def user {:email "useremail@example.com"
           :password "pass"})

(def token token)

(deftest link-test
  (testing "Links should be saved"
    (let [token (-> (profile)
                   :aviana-auth-url
                   (str "/user/signin")
                   (c/post :body user
                           :headers {:app_key (-> (profile) :aviana-api-key)})
                   :body
                   :token)]
      (def token token)
      (is (= "Unauthorized" (c/get "http://localhost:9000/links/abcd")))
      (is (= 200 (:status (c/post "http://localhost:9000/links"
                                  :body link
                                  :headers {:authorization (str "Token " token)}))))
      (is (some? (c/get "http://localhost:9000/links/abcd"
                        {:headers {:authorization (str "Token " token)}}))))))
