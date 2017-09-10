(ns links.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [links.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[links started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[links has shut down successfully]=-"))
   :middleware wrap-dev})
