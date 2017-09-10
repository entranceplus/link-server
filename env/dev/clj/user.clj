(ns user
  (:require [mount.core :as mount]
            [links.figwheel :refer [start-fw stop-fw cljs]]
            links.core))

(defn start []
  (mount/start-without #'links.core/repl-server))

(defn stop []
  (mount/stop-except #'links.core/repl-server))

(defn restart []
  (stop)
  (start))


