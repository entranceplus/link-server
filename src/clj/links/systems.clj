(ns links.systems
  (:require
   [links.handler :refer [app]]
   [environ.core :refer [env]]
   [system.core :refer [defsystem]]
   (system.components
    [jetty :refer [new-jetty new-web-server]]
    [postgres :refer [new-postgres-database]]
    [immutant-web :refer [new-immutant-web]]
    [repl-server :refer [new-repl-server]])))

(def prep-pg-db
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname ""
   :host (:dbhost env)
   :user (:dbuser env)
   :dbname (:dbname env)
   :password (:dbpassword env)})

(defsystem dev-system
  [:web (new-immutant-web :port (Integer. (env :http-port))
                          :handler app)
   :db (new-postgres-database prep-pg-db)])

;; (defsystem prod-system
;;   [:web (new-web-server (Integer. (env :http-port)) app)
;;    :repl-server (new-repl-server (Integer. (env :repl-port)))])

(defsystem prod-system
  [:web (new-immutant-web :port (Integer. (env :http-port))
                          :handler app)
   :db (new-postgres-database prep-pg-db)])
