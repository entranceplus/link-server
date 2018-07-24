(ns links.domain
  (:require [clojure.spec.alpha :as s]
            [compojure.core :refer [routes GET POST ANY]]
            [clojure.java.io :as io]
            [links.db.core :as db]
            [links.util :as util]
            [ring.util.http-response :as response]
            [clojure.tools.logging :as log]
            [buddy.auth :refer [authenticated? throw-unauthorized]]))

;; (defroutes link-routes
;;   (POST "/links" {:keys [headers body]}
;;         (db/add-link  body (get headers "x-authenticated-userid"))
;;         (util/ok-response {:msg "Links recordedd"}))
;;   (GET "/links" {:keys [headers]}
;;        (util/ok-response
;;         (get-links (get headers "x-authenticated-userid"))))
;;   )

(defn with-authentication [req handler]
  (if (authenticated? req)
    (handler req)
    (throw-unauthorized {:message "Not authorized"})))

(defn link-routes [{{conn :store}  :db}]
  (routes
   (POST "/links" {:keys [headers params] :as req}
         (with-authentication req 
           (fn [req]
             (let [{:keys [url tags]} params
                   user-id (-> req :identity :_id)]
               (if-let [link (db/add-link conn {:links/url url
                                                :user/id user-id
                                                :links/tags tags})]
                 (util/ok-response {:msg "Link added"
                                    :link link})
                 (util/ok-response
                  {:msg "Link was already there"}))))))
   (GET "/links/:user-id" [user-id :as req]
        (with-authentication req
          (fn [req]
            (let [user-id (-> req :identity :_id)]
              (util/ok-response (db/get-links conn user-id))))))))

(defn home-page []
  (-> "home.html"
     io/resource
     slurp
     response/ok
     (response/header "Content-Type" "text/html")))

(defn site [_]
  (routes
   (GET "/" [] (home-page))
   (ANY "*" [] (home-page))))

;; (save-link {:id "asdasd"
;;             :url "http://dsfdfdsfs.com"
;;             :tags ["sadas"]}
;;            "52ed24e2-7f65-458f-9f19-b9b5b353c5af")
