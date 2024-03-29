(ns goban.controller
  (:use [goban.model :only (state)])
  (:require [one.dispatch :as dispatch]
            [goban.board :as board]))

(defn get-changes
  "Given a board and a move (a stone color and position), determine the
  changes that need to be reflected in the board when the move is
  made:

  - Gather the group rooted at the new stone.

  - Gather the groups rooted at each opposing stone adjacent to the
  new stone.

  - Count the number of liberties of the friendly group.

  - Count the number of liberties of each opposing group.

  - If the friendly group has no liberties, and every opposing group
  has one or more liberty, the move is illegal (suicide).

  - For each opposing group with zero liberties, remove the member
  stones.

  Returns a vector of changes [:add/:remove :black/:white [x y]]"
  [board color pos]
  (if (board pos)
    []
    (let [new-board (assoc board pos color)
          new-group (board/collect-group new-board pos)
          adj-groups (set (map (partial board/collect-group new-board)
                               (filter #(and (board %)
                                             (not (= color (board %))))
                                       (board/adjacent-points pos (:size board)))))
          my-liberties (board/count-group-liberties new-board new-group)
          [dead alive] (reduce (fn [[dead alive] group]
                                 (if (zero? (board/count-group-liberties new-board
                                                                         group))
                                   [(conj dead group) alive]
                                   [dead (conj alive group)]))
                               [[] []]
                               adj-groups)]

      (if (and (zero? my-liberties) (empty? dead))
        [] ; suicide
        (into [[:add color pos]]
              (map (fn [pos] [:remove (board pos) pos]) (apply concat dead)))))))

(defmulti action
  :type)

(defmethod action :init [_]
  (reset! state {:state :init
                 :board {:size 9} ; TODO
                 :turn :black}))

(dispatch/react-to #{:init}
                   (fn [t d] (action (assoc d :type t))))

(dispatch/react-to (fn [e] (= :click (first e)))
                   (fn [_ pos]
                     (let [changes (get-changes (:board @state) (:turn @state) pos)]
                       (when (not (empty? changes))
                         (dispatch/fire :update changes)
                         ; TODO
                         ))))
