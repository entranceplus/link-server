(ns links.domain
  (:require [clojure.spec.alpha :as s]
            [compojure.core :refer [defroutes GET POST]]
            [links.db.core :as db]
            [links.util :as util]
            [ring.util.http-response :as response]))

(defn save-tags
  "retreive details about the tags. if tag does not exists create it"
  [tags]
  (println "tags are " (count tags))
  (let [old-tags (db/get-tag-info tags)
        new-tags (filter (fn [tag]
                           (not-any? #(= tag (:title %))
                                     old-tags))
                         tags)]
    (->> new-tags
         db/add-tags
         seq
         (concat old-tags)
         distinct)))

(defn save-link [{:keys [url tags]} user-id]
  (if-let [link-id (db/add-link {:url url
                              :user_id user-id})]
    (->> tags
        save-tags
        (db/add-links-tags-rel link-id))
    (throw (ex-info "Link was not saved"
                    {:user-id user-id}))))

(defn group-links [links]
  (group-by (fn [x] (-> x :url))  links))

(defn collect-links [grouped-links]
  (map (fn [[url links]] {:url url
                         :tags (reduce (fn [acc l]
                                         (conj acc (:title l)))
                                       [] links)})
       grouped-links))

(def extract-links (comp collect-links group-links))

(defn get-links [user-id]
  (-> user-id
      db/get-links
      extract-links))

(defroutes link-routes
  (POST "/links" {:keys [headers body]}
        (save-link body (get headers "x-authenticated-userid"))
        (util/ok-response {:msg "Links recordedd"}))
  (GET "/links" {:keys [headers]}
       (util/ok-response
        (get-links (get headers "x-authenticated-userid")))))


;; (save-link {:id "asdasd"
;;             :url "http://dsfdfdsfs.com"
;;             :tags ["sadas"]}
;;            "52ed24e2-7f65-458f-9f19-b9b5b353c5af")
