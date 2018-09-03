(ns links.providers.reddit
  (:require [snow.client :as client])
  (:import [com.entranceplus.craw.crawler Reddit]
           [com.entranceplus.craw.dto Subreddit]))

(def reddit-client (Reddit.))

(defn get-topics []
  (->> reddit-client
     .getSubreddits
     .getMetadata
     (map #(-> % .getSubreddit))))

#_(get-topics)

(def h {"User-Agent" "Mozilla/5.0 (Windows NT 6.1;) Gecko/20100101 Firefox/13.0.1"})

(defn sub-posts
  "get the posts of a subreddit"
  [name]
  (let [url (str "http://reddit.com/r/" name ".json")]
    (-> url
       (client/get {:headers h})
       (get-in [:data :children]))))

(defn post-info
  "select url and title from the post"
  [post]
  (-> post
     :data
     (select-keys  [:url :title])))

(defn fetch [name]
  (->> name
     sub-posts
     (map #(post-info %))))

(fetch "clojure")
