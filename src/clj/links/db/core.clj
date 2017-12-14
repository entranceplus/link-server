(ns links.db.core
  (:require [clojure.core.async :refer [<!!]]
            [snow.datomic :as d]
            [clojure.spec.alpha :as s]
            [expound.alpha :as e]
            [system.repl :refer [system]]
            [clojure.tools.logging :as log]))

(def link-schema
  '[{:db/ident :links/url
     :db/valueType :db.type/string
     :db/cardinality :db.cardinality/one
     :db/doc "The url of link"}

    {:db/ident :user/id
     :db/valueType :db.type/string
     :db/cardinality :db.cardinality/one
     :db/doc "user id of user saving the link"}

    {:db/ident :links/tags
     :db/valueType :db.type/string
     :db/cardinality :db.cardinality/many
     :db/doc "List of tags"}])

(defn load-schema [conn]
  (d/transact conn link-schema))

(s/def :links/url string?)
(s/def :user/id string?)
(s/def :links/tags (s/coll-of string?))

(s/def :links/data (s/keys :req [:links/url :user/id :links/tags]))

(defn link-present?
  "Checks the presence of url belonging to user-id"
  [conn url user-id]
  (empty? (d/query conn '[:find ?e
                          :where [?e :links/url url]
                          [?e :user/id user-id]])))

(defn add-link
  "add link to db if not exists returns nil if link already exists"
  [conn {url :links/url user-id :user/id :as link}]
  {:pre [(s/valid? (complement nil?) conn)
         (s/valid? :links/data link)]}
  (log/debug "link to be added " link)
  (when (link-present? conn url user-id)
    (d/transact conn link)))

;; todo add post spec checking
(defn get-links
  "get links belonging to a user id"
  [conn user-id]
  (d/query conn `[:find  (~'pull ?e ~'[*])
                  :where [?e :user/id ~user-id]]))

;; for repl experiments
(comment (def conn (-> system :db :conn))
         (add-link conn {:links/url "abc"
                         :user/id "ok"
                         :links/tags []})
         (get-links conn "ok"))
