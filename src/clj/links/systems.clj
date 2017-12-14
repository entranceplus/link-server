(ns links.systems
  (:require
   [com.stuartsierra.component :as component]
   [links.domain :refer [link-routes]]
   [environ.core :refer [env]]
   [system.core :refer [defsystem]]
   [ring.middleware.format :refer [wrap-restful-format]]
   [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
   [ring.logger :refer [wrap-with-logger]]
   [links.db.core :refer [load-schema]]
   (system.components
    [jetty :refer [new-jetty new-web-server]]
    [postgres :refer [new-postgres-database]]
    [immutant-web :refer [new-immutant-web]]
    [repl-server :refer [new-repl-server]]
    [endpoint :refer [new-endpoint]]
    [middleware :refer [new-middleware]]
    [handler :refer [new-handler]]
    [datomic :refer [new-datomic-db]])))

(def rest-middleware
  (fn [handler]
    (wrap-restful-format handler
                         :formats [:json-kw]
                         :response-options {:json-kw {:pretty true}})))


(defn dev-system []
  (component/system-map
   :db  (new-datomic-db "datomic:dev://localhost:4334/toy"
                        load-schema)
   :links (component/using
           (new-endpoint link-routes)
           [:db])
   :middleware (new-middleware
                {:middleware  [rest-middleware
                               [wrap-defaults api-defaults]
                               wrap-with-logger]})
   :handler (component/using
             (new-handler)
             [:links :middleware])
   :web (component/using
         (new-immutant-web :port (Integer. (env :http-port)))
         [:handler])))


;; (defsystem prod-system
;;   [:web (new-web-server (Integer. (env :http-port)) app)
;;    :repl-server (new-repl-server (Integer. (env :repl-port)))])


(defsystem prod-system
  [;; :web (new-immutant-web :port (Integer. (env :http-port))
   ;;                        :handler app)
   ;;:db (new-postgres-database (prep-pg-db))
   ])
