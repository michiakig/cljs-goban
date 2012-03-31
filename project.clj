(defproject goban "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [ring "1.0.0-RC1"]
                 [compojure "0.6.4"]
                 [enlive "1.0.0"]
                 [org.mozilla/rhino "1.7R3"]
                 [com.google.javascript/closure-compiler "r1592"]
                 [org.clojure/google-closure-library "0.0-790"]
                 [clj-stacktrace "0.2.4"]]
  :git-dependencies [["git://github.com/brentonashworth/one.git"]
                     ["https://github.com/clojure/clojurescript.git"
                      "886d8dc81812962d30a741d6d05ce9d90975160f"]
                     ["https://github.com/levand/domina.git"
                      "8933b2d12c44832c9bfaecf457a1bc5db251a774"]]
  :extra-classpath-dirs [".lein-git-deps/clojurescript/src/clj"
                         ".lein-git-deps/clojurescript/src/cljs"
                         ".lein-git-deps/domina/src/cljs"
                         ".lein-git-deps/one/src/lib/clj"
                         ".lein-git-deps/one/src/lib/cljs"
                         ".lein-git-deps/one/src/app/cljs"
                         ".lein-git-deps/one/src/app/clj"
                         ".lein-git-deps/one/src/app/cljs-macros"
                         "templates"
                         "src/app/cljs"
                         "src/app/cljs-macros"
                         "src/app/clj"]
  :repl-init goban.repl)
