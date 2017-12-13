(ns links.core
  (:require [links.handler :as handler]
            [luminus.repl-server :as repl]
            [luminus.http-server :as http]
            [luminus-migrations.core :as migrations]
            [links.config :refer [env]]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.tools.logging :as log]
            [mount.core :as mount]
            [links.systems :refer [prod-system]]
            [system.repl :refer [set-init! go]])
  (:gen-class))

(defn -main
  "Start a production system, unless a system is passed as argument (as in the dev-run task)."
  [& args]
  (set-init! #'prod-system)
  (go))
