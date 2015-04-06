(ns mazegen.rules)

;http://en.wikipedia.org/wiki/Maze_generation_algorithm
;http://en.wikipedia.org/wiki/Prim%27s_algorithm

(defn neighbors
  "Compute the neighbors of a given coordinate."
  [[i j]]
  (map vector
       ((juxt inc identity dec identity) i)
       ((juxt identity inc identity dec) j)))

(defn categorized-neighbors [start maze]
  (->> start neighbors
       (filter #(get-in maze %))
       (group-by #(empty? (get-in maze %)))))

(defn init [rows cols]
  (vec (take rows (repeat (vec (take cols (repeat #{})))))))

(defn open-wall [src dst maze]
  (case (map - dst src)
    [1 0] (assoc-in (update-in maze src conj :down) dst #{ :up })
    [-1 0] (assoc-in (update-in maze src conj :up) dst #{ :down })
    [0 1] (assoc-in (update-in maze src conj :right) dst #{ :left })
    [0 -1] (assoc-in (update-in maze src conj :left) dst #{ :right })))

(defn prim-gen [start rows cols]
  (loop [maze (update-in (init rows cols) start conj :start)
         frontier (into #{} (filter #(get-in maze %) (neighbors start)))]
    (if (empty? frontier)
      maze
      (let [dst (rand-nth (vec frontier))
            { f true s false } (categorized-neighbors dst maze)]
        (recur (open-wall (rand-nth s) dst maze)
               (into (disj frontier dst) f))))))

;(prn (prim-gen [0 0] 4 4))