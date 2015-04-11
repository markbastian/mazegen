(ns mazegen.swingui
  (:gen-class)
  (:require [mazegen.rules :as rules])
  (:import (javax.swing JFrame JPanel JButton BoxLayout Box)
           (java.awt BorderLayout Graphics2D Color ImageCapabilities)
           (java.awt.geom Rectangle2D$Double Path2D$Double Ellipse2D$Double)))

(defn draw-cell [p dx dy row col maze]
  (let [sx (* dx col)
        sy (* dy row)
        cell (get-in maze [row col])]
    (do
      (when (cell :start)
        (.append p (Ellipse2D$Double. sx sy dx dy) false))
      (when-not (cell [row (dec col)])
        (doto p (.moveTo sx sy) (.lineTo sx (+ sy dy))))
      (when-not (cell [row (inc col)])
        (doto p (.moveTo (+ sx dx) sy) (.lineTo (+ sx dx) (+ sy dy))))
      (when-not (cell [(dec row) col])
        (doto p (.moveTo sx sy) (.lineTo (+ sx dx) sy)))
      (when-not (cell [(inc row) col])
        (doto p (.moveTo sx (+ sy dy)) (.lineTo (+ sx dx) (+ sy dy)))))))

(defn panel [maze]
  (doto (proxy [JPanel] []
          (paint [g2d]
            (let [dx (/ (.getWidth this) (count (maze 0)))
                  dy (/ (.getHeight this) (count maze))
                  p (Path2D$Double.)
                  bg (Rectangle2D$Double. 0 0 (.getWidth this) (.getHeight this))]
              (do
                (doseq [row (range (count maze))]
                  (doseq [col (range (count (get maze row)))]
                    (draw-cell p dx dy row col maze)))
                (doto g2d
                  (.setPaint Color/WHITE)
                  (.fill bg)
                  (.setPaint Color/BLACK)
                  (.draw p))))))))

(defn launch [exit-behavior]
  (let [cells 20
        empty-maze (rules/create-empty cells cells)
        prim-maze (rules/prim-gen empty-maze [0 0])]
    (doto (JFrame. "Maze Generator")
      (.setSize 800 600)
      (.setDefaultCloseOperation exit-behavior)
      (.add (panel prim-maze) BorderLayout/CENTER)
      (.setVisible true))))

(defn -main [& args]
  (launch JFrame/EXIT_ON_CLOSE))

(launch JFrame/DISPOSE_ON_CLOSE)
