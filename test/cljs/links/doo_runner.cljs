(ns links.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [links.core-test]))

(doo-tests 'links.core-test)

