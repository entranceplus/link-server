(ns links.crud
  (:require [compojure.core :refer [defroutes POST]]
            [links.util :as util]))

(defroutes link-routes
  (POST "/links" {links :params}
        (util/send-todo)))
