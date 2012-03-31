(ns goban.config)

(def ^{:doc "config for goban app"}
  config {:src-root "src"
          :app-root "src/app/cljs"
          :top-level-package "goban"
          :js "public/javascripts"
          :dev-js-file-name "main.js"
          :prod-js-file-name "mainp.js"
          :dev-js ["goog.require('goban.core');"
                   "goog.require('goban.view');"
                   "goban.core.repl();"]
          :prod-js ["goog.require('goban.core');"]
          ;; list of clj files to watch for changes
          :reload-clj ["/goban/repl"
                       "/goban/config"
                       "/goban/dev_server"
                       "/goban/snippets"]})
