(ns goban.controller
  (:use [goban.model :only (state)])
  (:require [one.dispatch :as dispatch]))

(defmulti action
  :type)

(defmethod action :init [_]
  (reset! state {:state :init :board [] :turn :black}))

(dispatch/react-to #{:init}
                   (fn [t d] (action (assoc d :type t))))
