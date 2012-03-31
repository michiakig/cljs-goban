(ns goban.core
  (:require [clojure.browser.repl :as repl]
            [one.sample.logging :as logging]
            [one.logging :as logging-lib]))

(defn repl []
  (repl/connect "http://localhost:9000/repl"))

;; (js/alert "hello from clojurescript")

(logging-lib/start-display (logging-lib/console-output))