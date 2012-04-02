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

(defn render-stone
  "Adds a stone of color (a keyword) at x, y"
  [[color [x y]]]
  (let [img (single-node (color snippets))]
    (set-styles! img {:position "absolute"
                      :left (+ (* step (dec x)) offset-x)
                      :top (+ (* step (dec y)) offset-y)})
    (append! (.-body js/document) img)))

(defn render-board
  "Add all the stones to a board of size * size"
  [stones size]
  (doseq [[color pos] stones]
    (render-stone color pos offset-x offset-y)))

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
  [board ;; white black
   ]
  (let [board-node (single-node board)]
    (set-attr! board-node "id" "board")
    (append! (.-body js/document) board-node)))

(defmulti render
  "Accepts a map representing the state of the app and renders it"
  :state)

(defmethod render :init [m]
  (init-view (:board snippets) ;; (:white snippets) (:black snippets)
             )
  (render-board (:board m) (:size m))
  (add-event-listeners "board"))

(defmethod render :in-progress [m]
  (render-stone (last (:board m))))

(dispatch/react-to #{:state-change} (fn [_ m] (render m)))
