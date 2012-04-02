(ns goban.model
  (:require [one.dispatch :as dispatch]))

(def state (atom {}))

(add-watch state :state-change
           (fn [k r o n]
             (dispatch/fire :state-change n)))

(defn empty-point? [board-data pos]
  (empty? (filter (fn [[_ pos2]] (= pos2 pos))
                  board-data)))

(defn validate
  "Determine if a particular move is legal"
  [board-data pos
   ;; color
   ]
  (empty-point? board-data pos))

(defn place-stone
  "Update the given board's state with a stone in this position, if
there isn't one already."
  [
   ;; board
   pos]
  (if (validate (:board @state) pos)
    (swap! state assoc
           :board (conj (:board @state) [:black pos])
           :state :in-progress)
    (swap! state assoc :state :error :msg "already a stone there")))

(dispatch/react-to (fn [e] (= (first e) :click))
                   (fn [[_ id] pos]
                     (place-stone
                      ;; id
                      pos)))
