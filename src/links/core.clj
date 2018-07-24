(ns links.core
  (:require [links.systems :as sys]
            [snow.repl :as repl]
            [system.repl :refer [set-init! go]]
            [snow.env :refer [profile]])
  (:gen-class))

(defn -main
  "Start a production system, unless a system is passed as argument (as in the dev-run task)."
  [& args]
  (repl/start! sys/system-config))
