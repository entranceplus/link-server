(ns links.handler-test
  (:require [clojure.test :refer :all]
            [links.handler :refer :all]
            [links.utility :refer [GET get-body POST]]))

(deftest test-app
  (testing "main route"
    (let [response (GET "/" app)]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response (GET "/invalid" app)]
      (is (= 404 (:status response))))))
