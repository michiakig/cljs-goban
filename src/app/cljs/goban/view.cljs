(ns goban.view
  (:use [domina
         :only (
                append!
                by-class
                by-id
                children
                clone
                destroy!
                insert-after!
                nodes
                set-style!
                set-styles!
                single-node
                )])
  (:require-macros [goban.snippets :as snippets])
  (:require [one.dispatch :as dispatch]
            [goog.dom :as gdom]
            [clojure.browser.event :as event]))

(def snippets (snippets/snippets))
(def step 30)
(def offset-x 0)
(def offset-y 0)

(defn render-stone
  "Adds a stone of color (a keyword) at x, y"
  [color x y]
  (let [img (single-node (color snippets))]
    (set-styles! img {:position "absolute"
                      :left (+ (* step (dec x)) offset-x)
                      :top (+ (* step (dec y)) offset-y)})
    (append! (.-body js/document) img)))

(defn render-board
  "Add all the stones to a board of size * size"
  [stones size]
  ;; render the board itself
  (doseq [[color x y] stones]
    (render-stone color x y offset-x offset-y)))

(defn clear-board
  "Remove all the stones, leave the board"
  []
  (doseq [n (nodes [(by-class "white")
                    (by-class "black")])]
    (destroy! n)))

(defn click-to-position
  "Accepts a goog.dom.BrowserEvent and returns a vector of the x, y
  position on the board that was clicked"
  [evt]
  [(inc (quot (- (.-clientX evt) offset-x) step))
   (inc (quot (- (.-clientY evt) offset-y) step))])

(defn- add-event-listeners
  [board-id]
  (let [board (by-id board-id)]
    (event/listen board "click" #(dispatch/fire [:click board-id]
                                                (click-to-position %)))))

(add-event-listeners "board")

;; (dispatch/react-to #{[:click "board"]} (fn [_ [x y]] (.log js/console x y)))
