(ns mazegen.rules)

;http://en.wikipedia.org/wiki/Maze_generation_algorithm
;http://en.wikipedia.org/wiki/Prim%27s_algorithm

(defn neighbors
  "Compute the neighbors of a given coordinate."
  [[i j]]
  (map vector
       ((juxt inc identity dec identity) i)
       ((juxt identity inc identity dec) j)))

(defn in-maze? [[i j] maze]
  (and (<= 0 i (dec (count maze))) (<= 0 j (dec (count (maze i))))))

(defn neighbors-on-board [start maze]
  (filter #(in-maze? % maze) (neighbors start)))

(defn categorized-neighbors [start maze]
  (let [n (neighbors-on-board start maze)]
    (group-by #(nil? (get-in maze %)) n)))

(defn init [rows cols]
  (vec (take rows (repeat (vec (take cols (repeat nil)))))))

(defn open-wall [src dst maze]
  (case (map - dst src)
    [1 0] (assoc-in (update-in maze src conj :down) dst #{ :up })
    [-1 0] (assoc-in (update-in maze src conj :up) dst #{ :down })
    [0 1] (assoc-in (update-in maze src conj :right) dst #{ :left })
    [0 -1] (assoc-in (update-in maze src conj :left) dst #{ :right })))

(defn prim-gen [start w h]
  (loop [maze (assoc-in (init w h) start #{})
         frontier (into #{} (neighbors-on-board start maze))]
    (if (empty? frontier)
      maze
      (let [dst (rand-nth (vec frontier))
            { frontier-additions true potential-sources false }
            (categorized-neighbors dst maze)
            src (rand-nth potential-sources)]
        (recur (open-wall src dst maze)
               (into (disj frontier dst) frontier-additions))))))

;(def dim 10)
;(def maze (prim-gen [0 0] dim dim))

;(doseq [i (range dim)] (prn (maze i)))
