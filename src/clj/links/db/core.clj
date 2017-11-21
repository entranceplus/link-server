(ns links.db.core
  (:require [links.db.util :as dbutil]
            [links.util :as util]))

(defn add-link [link]
  (:id (first (dbutil/add :links_store link))))

(defn get-tag-info
  "collect ids of tags if present"
  [tags]
  (dbutil/query {:select [:id :title]
                 :from :tags
                 :where [:in :tags.title tags]}))

(defn get-links
  "get links belonging to user-id"
  [user-id]
  (dbutil/query {:select [:l.id :title :url]
                 :from [[:links_store :l]]
                 :join [[:links_tags_rel :ltr] [:= :l.id :ltr.link_id]
                        :tags [:= :tags.id :ltr.tag_id]]
                 :where [:= :l.user_id user-id]}))

(defn add-tags
  "add tags to db, this will throw exception if no-unique
  tags are being added"
  [tags]
  (dbutil/add :tags (map (fn [tag] {:title tag})
                         tags)))

(defn add-links-tags-rel
  "add provided tags relation with the given link id"
  [link-id tags]
  (dbutil/add :links_tags_rel
            (map (fn [tag]
                   {:link_id link-id
                    :tag_id (:id tag)}) tags)))
