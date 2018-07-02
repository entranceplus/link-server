(ns links.systems
  (:require
   [com.stuartsierra.component :as component]
   [links.domain :refer [link-routes]]
   [environ.core :refer [env]]
   [system.core :refer [defsystem]]
   [ring.middleware.format :refer [wrap-restful-format]]
   [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
   [ring.logger :refer [wrap-with-logger]]
   [snow.systems :as system]
   [buddy.auth.middleware :refer (wrap-authentication wrap-authorization)]
   [buddy.auth.backends :as backend]
   [snow.env :refer [profile]]
   (system.components
    [jetty :refer [new-jetty new-web-server]]
    [postgres :refer [new-postgres-database]]
    [immutant-web :refer [new-immutant-web]]
    [repl-server :refer [new-repl-server]]
    [http-kit :refer [new-http-kit]]
    [endpoint :refer [new-endpoint]]
    [middleware :refer [new-middleware]]
    [konserve :refer [new-konserve]]
    [handler :refer [new-handler]])))

(def rest-middleware
  (fn [handler]
    (wrap-restful-format handler
                         :formats [:json-kw]
                         :response-options {:json-kw {:pretty true}})))

(def secret (-> (profile) :secret))
(def backend (backend/jws {:secret secret}))

(defn system-config [config]
  [:db (new-konserve :type :filestore :path (config :db-path))
   :links (component/using
           (new-endpoint link-routes)
           [:db])
   :middleware (new-middleware
                {:middleware  [rest-middleware
                               [wrap-defaults api-defaults]
                               [wrap-authentication backend]
                               [wrap-authorization backend]
                               wrap-with-logger]})
   :handler (component/using
             (new-handler)
             [:links :middleware])
   :web (component/using
         (new-http-kit :port (system/get-port config :http-port))
         [:handler])])

(defn dev-system []
  (system/gen-system system-config))


;; (defsystem prod-system
;;   [:web (new-web-server (Integer. (env :http-port)) app)
;;    :repl-server (new-repl-server (Integer. (env :repl-port)))])


(defsystem prod-system
  [;; :web (new-immutant-web :port (Integer. (env :http-port))
   ;;                        :handler app)
   ;;:db (new-postgres-database (prep-pg-db))
   ])
