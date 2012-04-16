(ns goban.config)

(def ^{:doc "config for goban app"}
  config {:src-root "src"
          :app-root "src/app/cljs"
          :top-level-package "goban"
          :js "public/javascripts"
          :dev-js-file-name "main.js"
          :prod-js-file-name "mainp.js"
          :dev-js ["goog.require('goban.model');"
                   "goog.require('goban.view');"
                   "goog.require('goban.controller');"
                   "goog.require('goban.core');"
                   "goog.require('goban.board');"
                   "goban.core.start();"
                   "goban.core.repl();"]
          :prod-js ["goban.core.start();"]
          ;; list of clj files to watch for changes
          :reload-clj ["/goban/repl"
                       "/goban/config"
                       "/goban/dev_server"
                       "/goban/snippets"]})
