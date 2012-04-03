(ns goban.model
  (:require [one.dispatch :as dispatch]))

(def state (atom {}))

(add-watch state :state-change
           (fn [k r o n]
             (dispatch/fire :state-change n)))

(defn empty-point? [board-data pos]
  (empty? (filter (fn [[_ pos2]] (= pos2 pos))
                  board-data)))

(defn adjacent-points [[x y] size]
  (filter (fn [[x y]]
            (and (<= 1 x size)
                 (<= 1 y size)))
   [[(inc x) y] [(dec x) y] [x (inc y)] [x (dec y)]]))

(defn validate
  "Determine if a particular move is legal"
  [board-data pos
   ;; color
   ]
  (empty-point? board-data pos))

(defn next-turn [{color :turn}] (if (= color :black) :white :black))

(defn place-stone
  "Update the given board's state with a stone in this position, if
there isn't one already."
  [
   ;; board
   pos]
  (if (validate (:board @state) pos)
    (swap! state
           (fn [old]
             (let [turn (next-turn @state)
                   board (conj (:board @state) [(:turn @state) pos])]
               (assoc old
                 :board board
                 :state :in-progress
                 :turn turn))))
    (swap! state assoc :state :error :msg "already a stone there")))

(dispatch/react-to (fn [e] (= (first e) :click))
                   (fn [[_ id] pos]
                     (place-stone
                      ;; id
                      pos)))
