(ns mazegen.canvasui
  (:require [mazegen.rules :as rules]))

(defn draw-background [canvas ctx]
  (doto ctx
    (-> .-fillStyle (set! "#FFFFFF"))
    (.fillRect 0 0 (.-width canvas) (.-height canvas))))

(defn draw-cell [ctx dx dy row col maze]
  (let [sx (* dx col)
        sy (* dy row)
        cell (get-in maze [row col])]
    (do
      (when (cell :start)
        (.arc ctx (+ sx (* 0.5 dx)) (+ sy (* 0.5 dy)) (* 0.5 dx) 0 (* 2.0 Math/PI)))
      (when-not (cell [row (dec col)])
        (doto ctx (.moveTo sx sy) (.lineTo sx (+ sy dy))))
      (when-not (cell [row (inc col)])
        (doto ctx (.moveTo (+ sx dx) sy) (.lineTo (+ sx dx) (+ sy dy))))
      (when-not (cell [(dec row) col])
        (doto ctx (.moveTo sx sy) (.lineTo (+ sx dx) sy)))
      (when-not (cell [(inc row) col])
        (doto ctx (.moveTo sx (+ sy dy)) (.lineTo (+ sx dx) (+ sy dy)))))))

(defn draw-maze [ctx canvas maze]
  (let [dx (/ (.-width canvas) (count (maze 0)))
        dy (/ (.-height canvas) (count maze))]
    (do
      (draw-background canvas ctx)
      (-> ctx .-fillStyle (set! "#000000"))
      (doseq [row (range (count maze))]
        (doseq [col (range (count (get maze row)))]
          (draw-cell ctx dx dy row col maze))))))

(set!
  (.-onload js/window)
  (when (and js/document (.-getElementById js/document))
    (let [cells 20
          empty-maze (rules/create-empty cells cells)
          prim-maze (rules/prim-gen empty-maze [0 0])
          dfb-maze (rules/depth-first-gen empty-maze [0 0])
          prim-canvas (.getElementById js/document "primCanvas")
          prim-ctx (.getContext prim-canvas "2d")
          dfb-canvas (.getElementById js/document "dfbCanvas")
          dfb-ctx (.getContext dfb-canvas "2d")]
      (do
        (doto prim-ctx .beginPath (draw-maze prim-canvas prim-maze) .stroke)
        (doto dfb-ctx .beginPath (draw-maze dfb-canvas dfb-maze) .stroke)))))
