(ns mazegen.canvasui
  (:require [mazegen.rules :as rules]))

(defn draw-background [canvas ctx]
  (doto ctx
    (-> .-fillStyle (set! "#FFFFFF"))
    (.fillRect 0 0 (.-width canvas) (.-height canvas))))

(defn draw-cell [ctx sx sy dx dy cell]
  (do
    (when-not (cell :left) (doto ctx (.moveTo sx sy) (.lineTo sx (+ sy dy))))
    (when-not (cell :right) (doto ctx (.moveTo (+ sx dx) sy) (.lineTo (+ sx dx) (+ sy dy))))
    (when-not (cell :up) (doto ctx (.moveTo sx sy) (.lineTo (+ sx dx) sy)))
    (when-not (cell :down) (doto ctx (.moveTo sx (+ sy dy)) (.lineTo (+ sx dx) (+ sy dy))))))

(defn draw-maze [ctx canvas maze]
  (let [dx (/ (.-width canvas) (count (maze 0)))
        dy (/ (.-height canvas) (count maze))]
    (do
    (draw-background canvas ctx)
    (-> ctx .-fillStyle (set! "#000000"))
    (doseq [row (range (count maze))]
      (doseq [col (range (count (get maze row)))]
        (draw-cell ctx (* dx col) (* dy row) dx dy (get-in maze [row col])))))))

(set!
  (.-onload js/window)
  (when (and js/document (.-getElementById js/document))
    (let [cells 20
          maze (rules/prim-gen [0 0] cells cells)
          canvas (.getElementById js/document "mazeCanvas")
          ctx (.getContext canvas "2d")]
      (doto ctx
        .beginPath
        (draw-maze canvas maze)
        .stroke))))
