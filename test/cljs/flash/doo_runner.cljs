(ns flash.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [flash.core-test]))

(doo-tests 'flash.core-test)
