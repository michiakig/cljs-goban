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
                set-attr!
                )])
  (:require-macros [goban.snippets :as snippets])
  (:require [one.dispatch :as dispatch]
            [goog.dom :as gdom]
            [clojure.browser.event :as event]))

(def snippets (snippets/snippets))
(def step 30)
(def offset-x 0)
(def offset-y 0)

(defn pos->str [[x y]]
  (let [x (if (< x 10) (str 0 x) x)
        y (if (< y 10) (str 0 y) y)]
    (str x y)))

(defn mk-stone-dom
  "Makes an <img> elt for a stone of color at (x,y)"
  [color [x y]]
  (doto (single-node (color snippets))
    (set-styles! {:position "absolute"
                  :left (+ (* step (dec x)) offset-x)
                  :top (+ (* step (dec y)) offset-y)})
    (set-attr! "id" (pos->str [x y])))) ; bit of a hack for now

(defn mk-empty-board-dom
  "Makes an <img> elt for an empty board"
  [board-size]
  (doto (single-node (board-size snippets))
    (set-attr! "id" "board")))

(defn clear-board!
  "Remove all the stones, leave the board"
  []
  (doseq [n (nodes [(by-class "white")
                    (by-class "black")])]
    (destroy! n)))

(defn click->pos
  "Convert a goog.dom.BrowserEvent to a vector of (x,y) position"
  [evt]
  [(inc (quot (- (.-clientX evt) offset-x) step))
   (inc (quot (- (.-clientY evt) offset-y) step))])

(defn render-empty-board-once!
  []
  (when-not (by-id "board")
    (let [board (mk-empty-board-dom :board)]
      (event/listen board "click" #(dispatch/fire [:click "board"] (click->pos %)))
      (append! (.-body js/document) board))))

(defn render-stone-once!
  [color pos]
  (when-not (by-id (pos->str pos))
    (append! (.-body js/document) (mk-stone-dom color pos))))

(defn render-all-stones-once!
  [stones]
  (doseq [[pos color] stones]
    (render-stone-once! color pos)))

(defn remove-stone!
  "Remove the stone at pos, if it exists"
  [pos]
  (when-let [img (by-id (pos->str pos))]
    (destroy! img)))

(defmulti render
  "Accepts a map representing the state of the app and renders it"
  :state)

(let [r (fn [m]
          (render-empty-board-once!)
          (clear-board!)
          (render-all-stones-once! (dissoc (:board m) :size)))]
  (defmethod render :init [m] (r m))
  (defmethod render :in-progress [m] (r m)))

(dispatch/react-to #{:state-change} (fn [_ m] (render m)))
