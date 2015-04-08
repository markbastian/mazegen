(ns mazegen.rules)

;http://en.wikipedia.org/wiki/Maze_generation_algorithm
;http://en.wikipedia.org/wiki/Prim%27s_algorithm

(defn neighbors
  "Compute the neighbors of a given coordinate."
  [maze [i j]]
  (->> (map vector
            ((juxt inc identity dec identity) i)
            ((juxt identity inc identity dec) j))
       (filter #(get-in maze %))))

(defn categorized-neighbors [start maze]
  (->> start (neighbors maze)
       (group-by #(empty? (get-in maze %)))))

(defn init [rows cols]
  (vec (take rows (repeat (vec (take cols (repeat #{})))))))

(defn open-wall [src dst maze]
  (-> maze
      (update-in src conj (map - dst src))
      (update-in dst conj (map - src dst))))

(defn prim-gen [start rows cols]
  (loop [maze (update-in (init rows cols) start conj :start)
         frontier (into #{} (neighbors maze start))]
    (if (empty? frontier)
      maze
      (let [dst (rand-nth (vec frontier))
            { f true s false } (categorized-neighbors dst maze)]
        (recur (open-wall (rand-nth s) dst maze)
               (into (disj frontier dst) f))))))

(defn dfs [start rows cols]
  (loop [maze (update-in (init rows cols) start conj :start) visited [start]]
    (if (empty? visited)
      maze
      (let [f (filter #(empty? (get-in maze %)) (neighbors maze (first visited)))]
        (if (empty? f)
          (recur maze (rest visited))
          (let [dst (rand-nth (vec f))]
            (recur (open-wall (first visited) dst maze) (conj visited dst))))))))

;(prn (prim-gen [0 0] 4 4))

;(prn (dfs [0 0] 4 4))