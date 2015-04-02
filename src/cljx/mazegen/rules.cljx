(ns mazegen.rules)

;http://en.wikipedia.org/wiki/Maze_generation_algorithm
;http://en.wikipedia.org/wiki/Prim%27s_algorithm

(defn neighbors [[i j]]
  (let [x ((juxt inc identity dec identity) i)
        y ((juxt identity inc identity dec) j)]
    (map vector x y)))

(defn prim-gen [w h]
  (loop [halls { [0 0] #{} } walls (neighbors [0 0]) locked {}]
    (if true
      3
      (recur {} {} {}))))

(defn gen-cell[](if (> (Math/random) 0.7) :alive :dead))

(defn seed-grid [rows cols]
  (vec (take rows (repeatedly (fn [] (vec (take cols (repeatedly gen-cell))))))))
