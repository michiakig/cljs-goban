(ns goban.snippets
  "Macros for including HTML snippets in the ClojureScript application
  at compile time. (see the one.sample.snippets namespace)"
  (:use [one.templates :only (render)])
  (:require [net.cgrand.enlive-html :as html]))

(defn- snippet [file id]
  (render (html/select (html/html-resource file) id)))

(defmacro snippets
  "Expands to a map of HTML snippets which are extracted from the
  design templates."
  []
  {:white (snippet "board.html" [:img.white])
   :black (snippet "board.html" [:img.black])
   :star (snippet "board.html" [:img.star])
   :point (snippet "board.html" [:img.point])
   :space (snippet "board.html" [:img.space])})
