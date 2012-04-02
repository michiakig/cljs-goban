(ns goban.core
  (:require [clojure.browser.repl :as repl]
            [one.sample.logging :as logging]
            [one.logging :as logging-lib]
            [one.dispatch :as dispatch]))

(defn ^:export start
  []
  (dispatch/fire :init))

(defn repl []
  (repl/connect "http://localhost:9000/repl"))

(logging-lib/start-display (logging-lib/console-output))
