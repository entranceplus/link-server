(ns links.domain
  (:require [clojure.spec.alpha :as s]
            [compojure.core :refer [defroutes GET POST]]
            [links.db.core :as db]
            [links.util :as util]))

(s/def ::id string?)
(s/def ::link uri?)

(s/def ::tag (s/and string?
                    (complement empty?)))

(s/def ::tags (s/coll-of ::tag
                         :distinct true))

(s/valid? ::tag "")

(s/def ::link-data (s/keys :req-un [::id ::link ::tags]))

;; (def a-tag {:id "asdasd"
;;             :link "http://dsfdfdsfs.com"
;;             :tags ["asd" "sadas"]})


;; (let [parsed (s/conform ::link-data a-tag)]
;;   (if (= parsed ::s/invalid)
;;     (throw (ex-info "Invalid input"
;;                     {:reason (expound/expound-str ::link-data a-tag)}))
;;     (println (db/query {:select :id
;;                         :from :users}))))

(defn add-link [link]
  (:id (first (db/add :links_store link))))

(defn get-tag-info
  "collect ids of tags if present"
  [tags]
  (db/query {:select [:id :title]
             :from :tags
             :where [:in :tags.title tags]}))

(defn get-links-fromdb
  "get links belonging to user-id"
  [user-id]
  (db/query {:select [:l.id :title :url]
             :from [[:links_store :l]]
             :join [[:links_tags_rel :ltr] [:= :l.id :ltr.link_id]
                    :tags [:= :tags.id :ltr.tag_id]]
             :where [:= :l.user_id user-id]}))

(defn add-tags
  "add tags to db, this will throw exception if no-unique
  tags are being added"
  [tags]
  (db/add :tags (map (fn [tag] {:title tag})
                     tags)))

(defn save-tags
  "retreive details about the tags. if tag does not exists create it"
  [tags]
  (let [old-tags (get-tag-info tags)
        new-tags (filter (fn [tag]
                           (not-any? #(= tag (:title %))
                                     old-tags))
                         tags)]
    (if (empty? new-tags)
      old-tags
      (merge old-tags
             (add-tags new-tags)))))

(defn save-link [{:keys [url tags]} user-id]
  (if-let [link-id (add-link {:url url
                              :user_id user-id})]
    (db/add :links_tags_rel
            (map (fn [tag]
                   {:link_id link-id
                    :tag_id (:id tag)}) (save-tags tags)))
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
      get-links-fromdb
      extract-links))

(defroutes link-routes
  (POST "/links" {:keys [headers params]}
        (save-link params (get headers "x-authenticated-userid"))
        (util/ok-response {:msg "Links recorded"}))
  (GET "/links" {:keys [headers]}
       (util/ok-response
        (get-links (get headers "x-authenticated-userid")))))


;; (save-link {:id "asdasd"
;;             :url "http://dsfdfdsfs.com"
;;             :tags ["asd" "sadas"]}
;;            "52ed24e2-7f65-458f-9f19-b9b5b353c5af")
