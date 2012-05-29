(ns goban.board)

(defn flat-board-to-map
  "Accepts a seq representing a Go board in a human-friendly format.
  The length of the seq should be size of the board squared, and each
  element represents either an empty point, a black stone or a white
  stone. For instance, 3x3 board:
  [. w .
   . . .
   . . b]
  Returns a map from [x,y] positions to stone colors."
  ([flat] (flat-board-to-map flat '. 'b 'w))
  ([flat space black white]
     (let [size (Math/sqrt (count flat))]
       (loop [flat flat
              col 1
              row 1
              acc {:size size}]
         (if flat
           (recur
            (next flat)
            (if (< col size) (inc col) 1)
            (if (< col size) row (inc row))
            (if (or (= (first flat) black)
                    (= (first flat) white))
              (assoc acc [col row] (first flat))
              acc))
           acc)))))

(defn map-to-flat-board
  "Accepts a map of [x,y] positions to stone colors and returns a
  human friendly-formatted board"
  ([m] (map-to-flat-board m '.))
  ([m space]
     (vec (for [row (range 1 (inc (:size m)))
                col (range 1 (inc (:size m)))]
            (m [col row] space)))))

(defprotocol IPrettyPrint
  (pretty-print [this]))

(let [pprint-flat (fn [flat]
                    (doseq [row (partition (Math/sqrt (count flat)) flat)]
                      (let [s (pr-str row)]
                        (println (.substring s 1 (dec (count s)))))))
      pprint-map (fn [m]
                   (pprint-flat (map-to-flat-board m (:size m))))]
  (extend-protocol IPrettyPrint
    cljs.core.HashMap
    (pretty-print [this] (pprint-map this))
    cljs.core.ObjMap
    (pretty-print [this] (pprint-map this))
    cljs.core.Vector
    (pretty-print [this] (pprint-flat this))))

(defn adjacent-points [[x y] size]
  (filter (fn [[x y]]
            (and (<= 1 x size)
                 (<= 1 y size)))
   [[(inc x) y] [(dec x) y] [x (inc y)] [x (dec y)]]))

(defn collect-group
  "Accepts a board and a position and returns a list of positions of
  stones in the group the argument position if a member of."
  ([board pos]
     (collect-group board (board pos) [pos] #{} []))
  ([board color q visited acc]
     (if (empty? q)
       acc
       (let [neighbors
             (filter (fn [pos] (and (= (board pos) color)
                                    (not (visited pos))))
                     (adjacent-points (peek q) (:size board)))]
         (collect-group
          board
          color
          (into (pop q) neighbors)
          (conj visited (peek q))
          (conj acc (peek q)))))))

(defn empty-adjacent-points
  [board pos]
  (filter #(not (board %)) (adjacent-points pos (:size board))))

(defn count-liberties
  [board pos]
  (count (empty-adjacent-points board pos)))

(defn count-group-liberties
  [board group]
  (count (reduce (fn [acc pos]
                   (let [it (empty-adjacent-points board pos)]
                     (if (empty? it)
                       acc
                       (apply conj acc it))))
                 #{}
                 group)))