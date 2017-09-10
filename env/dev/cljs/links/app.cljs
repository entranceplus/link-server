(ns ^:figwheel-no-load links.app
  (:require [links.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
