(ns links.app
  (:require [snow.comm.core :as comm]))

(defn main []
  (comm/start!)
  (println "Well.. Hello there!!!!"))


(main)
