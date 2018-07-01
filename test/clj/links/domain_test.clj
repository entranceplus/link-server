(ns links.domain-test
  (:require [snow.client :as c]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [links.utility :as util]))

(def token (atom ""))

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


(deftest link-test
  (testing "Links should be saved"
    (is (= 200 (:status (c/post "http://localhost:9000/links"
                                :body link))))
    (is (some? (c/get "http://localhost:9000/links/abcd")))))
