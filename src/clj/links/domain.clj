(ns links.domain
  (:require [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
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
  (:id (first (db/add :links link))))

(defn get-tag-info
  "collect ids of tags if present"
  [tags]
  (db/query {:select [:id :title]
             :from :tags
             :where [:in :tags.title tags]}))

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

;; (def tags ["aasassa" "Asweasss"])

;; (add-tags (filter (fn [tag]
;;                     (not-any? #(= tag (:title %))
;;                               (get-tag-info tags)))
;;                   tags))

;; (get-tag-info tags)
;; (save-tags tags)

(defn save-link [{:keys [url tags]} user-id]
  (let [link-id (add-link {:url url
                           :user_id user-id})]
    (db/add :links_tags_rel
            (map (fn [tag]
                   {:link_id link-id
                    :tag_id (:id tag)}) (save-tags tags)))))

;; (save-link {:id "asdasd"
;;             :url "http://dsfdfdsfs.com"
;;             :tags ["asd" "sadas"]}
;;            "f0702fe5-a828-488f-8a54-d8e2c25af854")
