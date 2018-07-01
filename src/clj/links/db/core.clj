(ns links.db.core
  (:require [clojure.core.async :refer [<!!]]
            [snow.db :as db]
            [snow.repl :as r]
            [clojure.spec.alpha :as s]
            [expound.alpha :as e]
            [system.repl :refer [system]]
            [clojure.tools.logging :as log]))

(s/def :links/url string?)
(s/def :user/id string?)
(s/def :links/tags (s/coll-of string?))

(s/def :links/data (s/keys :req [:links/url :user/id :links/tags]))

;; todo add post spec checking
(defn get-links
  "get links belonging to a user id"
  [conn user-id]
  (->> :links/data
     (db/get-entity conn)
     (filter #(= user-id (:user/id %)))))

(defn link-present?
  "Checks the presence of url belonging to user-id"
  [conn url user-id]
  (->> user-id
     (get-links conn)
     (filter #(= url (:links/url %)))
     not-empty))

(defn add-link
  "add link to db if not exists returns nil if link already exists"
  [conn {url :links/url user-id :user/id :as link}]
  {:pre [(s/valid? (complement nil?) conn)
         (s/valid? :links/data link)]}
  (println "link to be added " link)
  (when-not (link-present? conn url user-id)
    (db/add-entity conn :links/data link)))


;; for repl experiments
(comment (def conn (-> @r/state :snow.systems/repl first :db :store))
         (link-present? conn "http://ep.in" "abcd")
         (add-link conn {:links/url "abc"
                         :user/id "ok"
                         :links/tags []})
         (get-links conn "abcd")
         (delete-entity conn :links/data ))
