(ns links.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[links started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[links has shut down successfully]=-"))
   :middleware identity})
