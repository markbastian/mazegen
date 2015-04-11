(ns mazegen.swingui
  (:gen-class)
  (:require [mazegen.rules :as rules])
  (:import (javax.swing JFrame JPanel JButton BoxLayout Box)
           (java.awt BorderLayout Graphics2D Color ImageCapabilities)
           (java.awt.geom Rectangle2D$Double Path2D$Double Ellipse2D$Double)
           (java.awt.event ActionListener)))

(defn draw-cell [p dx dy row col maze]
  (let [sx (* dx col)
        sy (* dy row)
        cell (get-in maze [row col])]
    (do
      (when (or (cell :start) (cell :end))
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
  (doto
    (proxy [JPanel] []
    (paint [g2d]
      (let [dx (/ (.getWidth this) (count (@maze 0)))
            dy (/ (.getHeight this) (count @maze))]
        (doto g2d
          (.setPaint Color/WHITE)
          (.fill (Rectangle2D$Double. 0 0 (.getWidth this) (.getHeight this)))
          (.setPaint Color/BLACK)
          (.draw (doto (Path2D$Double.)
                   (#(doseq [row (range (count @maze))]
                      (doseq [col (range (count (get @maze row)))]
                        (draw-cell % dx dy row col @maze))))))))))
    (#(add-watch maze :maze-changed (fn [_ _ _ _] (.repaint %))))))

(defn launch [exit-behavior]
  (let [cells 20
        start [0 0]
        end [(dec cells) (dec cells)]
        empty-maze (rules/create-empty cells cells)
        maze (atom (rules/prim-gen empty-maze start end))]
    (doto (JFrame. "Maze Generator")
      (.setSize 800 600)
      (.setDefaultCloseOperation exit-behavior)
      (.add (panel maze) BorderLayout/CENTER)
      (.add (doto (Box. BoxLayout/LINE_AXIS)
              (.add (doto (JButton. "Generate Prim")
                      (.addActionListener
                        (reify ActionListener (actionPerformed [_ _]
                                                (reset! maze (rules/prim-gen empty-maze start end)))))))
              (.add (doto (JButton. "Generate Recursive Backtracking")
                      (.addActionListener
                        (reify ActionListener (actionPerformed [_ _]
                                                (reset! maze (rules/depth-first-gen empty-maze start end)))))))) BorderLayout/SOUTH)
      (.setVisible true))))

(defn -main [& args]
  (launch JFrame/EXIT_ON_CLOSE))

(launch JFrame/DISPOSE_ON_CLOSE)
