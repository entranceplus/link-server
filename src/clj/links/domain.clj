(ns links.domain
  (:require [clojure.spec.alpha :as s]
            [compojure.core :refer [routes GET POST]]
            [links.db.core :as db]
            [links.util :as util]
            [ring.util.http-response :as response]
            [clojure.tools.logging :as log]))

;; (defroutes link-routes
;;   (POST "/links" {:keys [headers body]}
;;         (db/add-link  body (get headers "x-authenticated-userid"))
;;         (util/ok-response {:msg "Links recordedd"}))
;;   (GET "/links" {:keys [headers]}
;;        (util/ok-response
;;         (get-links (get headers "x-authenticated-userid"))))
;;   )
(defn link-routes [{{conn :conn} :db}]
  (routes
   (POST "/links" {:keys [headers params]}
         (let [{:keys [url user-id tags]} params]
           (if-let [link (db/add-link conn {:links/url url
                                            :user/id user-id
                                            :links/tags tags})]
             (util/ok-response {:msg "Link added"
                                :link link})
             (util/ok-response
              {:msg "Link was already there"}))))
   (GET "/links" []
        (util/ok-response (db/get-links conn "ki")))))

;; (save-link {:id "asdasd"
;;             :url "http://dsfdfdsfs.com"
;;             :tags ["sadas"]}
;;            "52ed24e2-7f65-458f-9f19-b9b5b353c5af")
