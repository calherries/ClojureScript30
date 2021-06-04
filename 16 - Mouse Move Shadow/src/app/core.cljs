(ns app.core
  (:require [lambdaisland.dom-types]
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

(def hero (js/document.querySelector ".hero"))

(def text (.querySelector hero "h1"))

(def walk 500) ; px

(defn shadow [e]
  (let [{width  :offsetWidth
         height :offsetHeight} (->clj hero)
        {x      :offsetX
         y      :offsetY
         target :target}       (->clj e)
        not-target             (not= (js-this) target) ;; TODO does this work?
        x                      (cond-> x not-target (+ (.-offsetLeft target)))
        y                      (cond-> y not-target (+ (.-offsetTop target)))
        x-walk                 (js/Math.round (- (/ (* x walk)
                                                    width)
                                                 (/ walk 2)))
        y-walk                 (js/Math.round (- (/ (* y walk)
                                                    height)
                                                 (/ walk 2)))
        shadow-style           (str x-walk     "px " y-walk     "px 0 rgba(255,0,255,0.7),"
                                    (- x-walk) "px " y-walk     "px 0 rgba(0,255,255,0.7),"
                                    y-walk     "px " (- x-walk) "px 0 rgba(0,255,0,0.7),"
                                    (- y-walk) "px " x-walk     "px 0 rgba(0,0,255,0.7)")]
    (set! (-> text .-style .-textShadow) shadow-style)))

(.addEventListener hero "mousemove" shadow)