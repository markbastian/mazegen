(ns mazegen.rules)

;http://en.wikipedia.org/wiki/Maze_generation_algorithm
;http://en.wikipedia.org/wiki/Prim%27s_algorithm

(defn neighbors
  "Compute the neighbors of a given coordinate."
  [[i j]]
  (map vector
       ((juxt inc identity dec identity) i)
       ((juxt identity inc identity dec) j)))

(defn valid [[i j] [w h]] (and (<= 0 i (dec w)) (<= 0 j (dec h))))

(defn expand [start current w h]
  (let [valid-neighbors (filter #(valid % [w h]) (neighbors start))]
    (into current (map vector (repeat start) valid-neighbors))))

(defn prim-gen [start w h]
  (loop [maze { start [] } frontier (expand start [] w h)]
    (if (empty? frontier)
      maze
      (let [[k v] (rand-nth frontier)
            new-maze (into (update-in maze [k] conj v) { v []})
            visited (into #{} (keys new-maze))]
        (recur new-maze (vec (filter #(not (visited (second %))) (expand v frontier w h))))))))

(def maze (prim-gen [0 0] 100 100))
;;Now, we just need to render. For every cell, you track the input side (always open) and the exits (also open).
;;Everything else is a line.