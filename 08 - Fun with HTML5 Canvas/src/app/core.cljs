(ns app.core)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Dev utils

(defonce debug (atom nil))
(add-tap #(reset! debug %))
(count @tapset)
(comment
  @debug)

(defn cljs
  "For cases when built-in js->clj doesn't work. Source: https://stackoverflow.com/a/32583549/4839573"
  [x]
  (into {} (for [k (js-keys x)]
             [(keyword k) (aget x k)])))

(def log
  "Shorter console.log"
  js/console.log)

;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Globals

(def canvas (js/document.querySelector "#draw"))
(def ctx (-> canvas (.getContext "2d")))
(set! (.-width canvas) js/window.innerWidth)
(set! (.-height canvas) js/window.innerHeight)
(set! (.-strokeStyle ctx) "#BADA55")
(set! (.-lineJoin ctx) "round")
(set! (.-lineCap ctx) "round")
(set! (.-lineWidth ctx) 100)

;; Check
(comment
  (log ctx))

(def state
  (atom
   {:is-drawing false
    :last-x     0
    :last-y     0
    :hue        0
    :direction true}))

(-> canvas
    (.addEventListener "mousedown"
                       #(swap! state merge {:is-drawing true
                                            :last-x     (.-offsetX %)
                                            :last-y (.-offsetY %)})))

(-> canvas
    (.addEventListener "mouseout" #(swap! state assoc :is-drawing false)))

(-> canvas
    (.addEventListener "mouseup" #(swap! state assoc :is-drawing false)))

(defn draw [e]
  (when (:is-drawing @state)
    (set! (.-strokeStyle ctx) (str "hsl(" (:hue @state) ", 100%, 50%)"))
    (.beginPath ctx)
    (.moveTo ctx (:last-x @state) (:last-y @state))
    (.lineTo ctx (.-offsetX e) (.-offsetY e))
    (.stroke ctx)
    (swap! state merge {:last-x (.-offsetX e)
                        :last-y (.-offsetY e)})
    (swap! state update :hue inc)
    (when (< 360 (:hue @state))
      (swap! state assoc :hue 0))
    (when (or (<= 100 (.-lineWidth ctx))
              (<= (.-lineWidth ctx) 1))
      (swap! state update :direction not))
    (if (:direction @state)
      (set! (.-lineWidth ctx) (inc (.-lineWidth ctx)))
      (set! (.-lineWidth ctx) (dec (.-lineWidth ctx))))
    ))

(-> canvas
    (.addEventListener "mousemove" draw))

(comment
  @state
  (log @debug))

(defn init []
  (js/console.log "init"))