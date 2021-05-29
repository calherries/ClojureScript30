(ns app.core
  (:require [oops.core :refer [oget oset! ocall! oapply! ocall oapply
                               oget+ oset!+ ocall!+ oapply!+ ocall+ oapply+
                               gget gset! gcall! gapply! gcall gapply
                               gget+ gset!+ gcall!+ gapply!+ gcall+ gapply+]]
            [lambdaisland.dom-types]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Dev utils

(defonce debug (atom nil))
(add-tap #(reset! debug %))
(count @tapset)
(comment
  @debug)

(defn ->clj
  "For cases when built-in js->clj doesn't work. Source: https://stackoverflow.com/a/32583549/4839573"
  [x]
  (into {} (for [k (js-keys x)]
             [(keyword k) (aget x k)])))

;;;;;;;;;;;;;;;;;
;;; Functions

(def player (js/document.querySelector ".player"))
(def video (js/document.querySelector ".viewer"))
(def toggle (js/document.querySelector ".toggle"))
(def progress (js/document.querySelector ".progress"))
(def progress-bar (js/document.querySelector ".progress__filled"))
(def skip-buttons (js/document.querySelectorAll "[data-skip]"))
(def ranges (js/document.querySelectorAll ".player__slider"))

(defn toggle-play []
  (if (.-paused video)
    (.play video)
    (.pause video)))

(defn update-button []
  (let [icon (if (.-paused (js-this)) "►" "❚ ❚")]
    (set! (.-textContent toggle) icon)))

(defn handle-progress []
  (let [percent-filled (str (* 100 (/ (.-currentTime video) (.-duration video)))
                            "%")]
    (set! (-> progress-bar .-style .-flexBasis) percent-filled)))

(defn skip []
  (this-as this
           (let [skip-seconds (+ (.-currentTime video) (js/parseFloat (-> this .-dataset .-skip)))]
             (set! (.-currentTime video) (+ (.-currentTime video) skip-seconds)))))

(defn handle-range-update []
  (this-as this
           (aset video (.-name this) (.-valueAsNumber this))
           (js/console.dir this)))

(defn scrub [e]
  (set! (.-currentTime video) (* (.-duration video)
                                 (/ (.-offsetX e)
                                    (.-offsetWidth progress)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Start

(doto video
  (.addEventListener "click" toggle-play)
  (.addEventListener "play" update-button)
  (.addEventListener "pause" update-button)
  (.addEventListener "timeupdate" handle-progress))

(.addEventListener toggle "click" toggle-play)
(run! #(.addEventListener % "click" skip) skip-buttons)
(run! #(.addEventListener % "change" handle-range-update) ranges)
(run! #(.addEventListener % "mousemove" handle-range-update) ranges)

(def is-mousedown (atom false))
(.addEventListener progress "click" scrub)
(.addEventListener progress "mousemove" #(when @is-mousedown (scrub %)))
(.addEventListener progress "mousedown" #(reset! is-mousedown true))
(.addEventListener progress "mouseup" #(reset! is-mousedown false))

(defn init []
  (js/console.log "init"))