(ns app.core
  (:require [lambdaisland.dom-types]
            [clojure.string :as str]
            [clojure.pprint]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Dev utils

(defonce debug (atom nil))
(add-tap #(reset! debug %))
(add-tap #(js/console.log %))
(comment
  @debug
  (add-tap #(clojure.pprint/pprint %))
  (reset! tapset nil)
  (count @tapset)
  (tap> "Hello, tap!"))

(defn ->clj
  "For cases when built-in js->clj doesn't work. Source: https://stackoverflow.com/a/32583549/4839573"
  [x]
  (into {} (for [k (js-keys x)]
             [(keyword k) (aget x k)])))

(defn init []
  (js/console.log "init"))

(def triggers (js/document.querySelectorAll "a"))

(def highlight (js/document.createElement "span"))

(-> highlight .-classList (.add "highlight"))

(js/document.body.appendChild highlight)

(defn highlight-link []
  (let [link-coords (->clj (.getBoundingClientRect (js-this)))
        coords {:width (:width link-coords)
                :height (:height link-coords)
                :top (+ (:top link-coords) (.-scrollY js/window))
                :left (+ (:left link-coords) (.-scrollX js/window))}]
    (set! (-> highlight .-style .-width) (str (:width coords) "px"))
    (set! (-> highlight .-style .-height) (str (:height coords) "px"))
    (set! (-> highlight .-style .-transform) (str "translate(" (:left coords) "px, " (:top coords) "px"))))

(doseq [trigger (array-seq triggers)]
  (.addEventListener trigger "mouseenter" highlight-link))