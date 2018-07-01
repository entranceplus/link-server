(ns user
  (:require [snow.repl :as repl]
            [links.systems :as sys]))


(repl/start! sys/system-config)

(repl/stop!)
