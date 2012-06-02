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

(defn render-added-stone
  "Adds a stone of color (a keyword) at x, y"
  [color [x y]]
  (let [img (single-node (color snippets))]
    (set-styles! img {:position "absolute"
                      :left (+ (* step (dec x)) offset-x)
                      :top (+ (* step (dec y)) offset-y)})
    (set-attr! img "id" (pos->str [x y])) ; bit of a hack for now
    (append! (.-body js/document) img)))

(defn render-removed-stone
  [[x y]]
  (when-let [img (by-id (str x y))]
    (destroy! img)))

(defn render-board
  "Add all the stones to a board of size * size"
  [stones size]
  (doseq [[pos color] stones]
    (render-added-stone color pos)))

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

(defn init-view
  [board]
  (let [board-node (single-node board)]
    (set-attr! board-node "id" "board")
    (append! (.-body js/document) board-node)))

(defmulti render
  "Accepts a map representing the state of the app and renders it"
  :state)

(defmethod render :init [m]
  (init-view (:board snippets))
  (render-board (dissoc (:board m) :size) (:size (:board m)))
  (add-event-listeners "board"))

(defmethod render :in-progress [m]
  (doseq [[change color pos]  (:last-changes m)]
    (cond (= change :add) (render-added-stone color pos)
          (= change :remove) (render-removed-stone pos))))

(dispatch/react-to #{:state-change} (fn [_ m] (render m)))
