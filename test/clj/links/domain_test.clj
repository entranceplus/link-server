(ns links.domain-test
  (:require [clj-http.client :as client]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [links.auth.core-test :refer [auth-user user]]
            [links.utility :as util]))

(def token (atom ""))

(use-fixtures
  :once (fn [f]
          (util/init)
          (reset! token (-> user
                            auth-user
                            util/get-body
                            :access_token))
          (f)))

(def link {:id "asdasd"
           :url "http://ep.in"
           :tags ["asd" "sadas" "aasdasdsd" "asdasd"]})


(deftest link-test
  (testing "Links should be saved"
    (is (= 200 (:status (client/post "https://links.entranceplus.in/links"
                                     {:form-params link
                                      :content-type :json
                                      :format :json
                                      :redirect-strategy :lax
                                      :headers {"Authorization"
                                                (str "Bearer " @token)}
                                      }))))))
