(ns goban.repl
  (:require [one.tools]
            [goban.dev-server]
            [clojure.java.browse :as browse]))

(defn start []
  (goban.dev-server/run-server)
  (future (Thread/sleep 3000)
          (browse/browse-url "http://localhost:8080/development"))
  (one.tools/cljs-repl))
