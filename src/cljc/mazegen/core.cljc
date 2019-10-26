(ns mazegen.core)

(defn create-empty
  "Create an empty rectangular maze."
  [rows cols]
  (vec (take rows (repeat (vec (take cols (repeat #{})))))))

(defn neighbors
  "Compute the neighbors of a given coordinate."
  [maze [i j]]
  (->> (map vector
            ((juxt inc identity dec identity) i)
            ((juxt identity inc identity dec) j))
       (filter #(get-in maze %))))

(defn open-wall
  "Create pathways between the src and dst coords of the maze."
  [maze src dst]
  (-> maze
      (update-in src conj dst)
      (update-in dst conj src)))

(defn prim-gen
  "Create a maze using Prim's method."
  [empty-maze start end]
  (loop [maze (update-in empty-maze start conj :start)
         frontier (into #{} (neighbors maze start))]
    (if (empty? frontier)
      (update-in maze end conj :end)
      (let [dst (rand-nth (vec frontier))
            n (neighbors maze dst)
            { f true s false } (group-by #(empty? (get-in maze %)) n)]
        (recur (open-wall maze (rand-nth s) dst)
               (into (disj frontier dst) f))))))

(defn depth-first-gen
  "Create a maze using a depth-first recursive search with backtracking."
  [empty-maze start end]
  (loop [maze (update-in empty-maze start conj :start)
         visited [start]]
    (if (empty? visited)
      (update-in maze end conj :end)
      (let [n (neighbors maze (first visited))
            f (filter #(empty? (get-in maze %)) n)]
        (if (empty? f)
          (recur maze (rest visited))
          (let [dst (rand-nth (vec f))]
            (recur (open-wall maze (first visited) dst)
                   (conj visited dst))))))))