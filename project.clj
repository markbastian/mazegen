(defproject mazegen "0.1.0-SNAPSHOT"
  :description "A project for generating mazes."
  :url "https://github.com/markbastian/mazegen"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"]]

  :main mazegen.swingui

  :jar-exclusions [#"\.swp|\.swo|\.DS_Store"]
  :profiles {:uberjar {:aot [mazegen.swingui]}
             :dev     {:plugins [[lein-cljsbuild "1.1.7"]
                                 [org.clojure/clojurescript "1.10.520"]]}
             :cljs    {:plugins [[lein-cljsbuild "1.1.7"]]}}

  :source-paths ["src/clj" "src/cljc" "test"]

  :cljsbuild {:builds [{:source-paths ["src/cljs" "src/cljc"]
                        :compiler     {:main          mazegen.canvasui
                                       :output-to     "resources/public/js/mazegen.js"
                                       :optimizations :advanced
                                       :pretty-print  true}}]})
