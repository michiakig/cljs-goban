(ns goban.test.board
  (:use [clojure.test]
        [one.test :only (cljs-eval cljs-wait-for *eval-env*)]
        [clojure.java.browse :only (browse-url)]
        [cljs.repl :only (-setup -tear-down)]
        [cljs.repl.browser :only (repl-env)]
        [goban.dev-server :only (run-server)]))

(defn setup
  "Start the development server and connect to the browser so that
  ClojureScript code can be evaluated from tests. Taken from
  one.sample.test.integration."
  [f]
  (let [server (run-server)
        eval-env (repl-env)]
    (-setup eval-env)
    (browse-url "http://localhost:8080/development")
    (binding [*eval-env* eval-env]
      (f))
    (-tear-down eval-env)
    (.stop server)))

(use-fixtures :once setup)

         ;; (= (cljs-eval goban.board (map-to-flat-board m)) f)

(defmacro are2
  "Checks multiple assertions with two template expressions. See clojure.test/are"
  [argv expr1 expr2 & rest]
  `(do
     (are ~argv ~expr1
          ~@rest)
     (are ~argv ~expr2
          ~@rest)))

(deftest test-flat-board-to-map
   (are2 [m f]
         (= m (cljs-eval goban.board (flat-board-to-map f)))
         (= f (cljs-eval goban.board (map-to-flat-board m)))
         {:size 3} '[. . .
                     . . .
                     . . .]
         {:size 3
          [1 1] 'b} '[b . .
                      . . .
                      . . .]
         {:size 3
          [3 3] 'w} '[. . .
                      . . .
                      . . w]))

(deftest test-collect-group
  (are [board pos group]
       (= group (cljs-eval goban.board (collect-group (flat-board-to-map board) pos)))
       '[b . .
         . . .
         . . .] [1 1] [[1 1]]

       '[w b w
         w b w
         w w w] [2 1] [[2 1] [2 2]]

       '[w b w
         w b w
         w w w] [1 1] [[1 1] [1 2] [1 3] [2 3] [3 3] [3 2] [3 1]]

         ))

(deftest test-count-liberties
  (are [board pos ls]
       (= ls (cljs-eval goban.board (count-liberties (flat-board-to-map board) pos)))
       '[b . .
         . . .
         . . .] [1 1] 2

       '[b w .
         . . .
         . . .] [1 1] 1

       '[. . .
         . b .
         . . .] [2 2] 4

       '[. w .
         w b w
         . w .] [2 2] 0
         ))

(deftest test-count-group-liberties
  (are [board group ls]
       (= ls (cljs-eval goban.board (count-group-liberties (flat-board-to-map board) group)))
       '[b . .
         . . .
         . . .] [[1 1]] 2

       '[b w .
         . . .
         . . .] [[1 1]] 1

       '[. . .
         . b .
         . . .] [[2 2]] 4

       '[. w .
         w b w
         . w .] [[2 2]] 0

       '[w w .
         w b .
         . . .] [[1 1] [1 2] [2 1]] 2
         ))