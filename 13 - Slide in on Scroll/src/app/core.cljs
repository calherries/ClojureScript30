(ns app.core
  (:require [lambdaisland.dom-types]
            [clojure.pprint]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Dev utils

(def debug (atom nil))
(comment
  @debug
  (add-tap #(reset! debug %))
  (add-tap #(js/console.log %))
  (add-tap #(clojure.pprint/pprint %))
  (reset! tapset nil)
  (count @tapset))

(defn ->clj
  "For cases when built-in js->clj doesn't work. Source: https://stackoverflow.com/a/32583549/4839573"
  [x]
  (into {} (for [k (js-keys x)]
             [(keyword k) (aget x k)])))

(defn debounce
  ([func]
   (debounce func {:wait      20
                   :immediate true}))
  ([func {:keys [immediate wait]}]
   (let [timeout (atom nil)]
     (fn []
       (let [this     (js-this)
             args     (js-arguments)
             later    (fn []
                        (reset! timeout nil)
                        (when-not immediate (.apply func this args)))
             call-now (and immediate (not @timeout))]
         (js/clearTimeout @timeout)
         (reset! timeout (js/setTimeout later wait))
         (when call-now (.apply func this args)))))))

(def slider-images
  (js/document.querySelectorAll ".slide-in"))

(defn check-slide []
  (doseq [slider-image slider-images]
    (let [slide-in-at (- (+ js/window.scrollY js/window.innerHeight)
                         (/ (.-height slider-image) 2))
          image-bottom (+ (.-offsetTop slider-image)
                          (.-height slider-image))
          is-half-shown (< (.-offsetTop slider-image) slide-in-at)
          is-not-scrolled-past (< js/window.scrollY image-bottom)]
      (if (and is-half-shown is-not-scrolled-past)
        (-> slider-image .-classList (.add "active"))
        (-> slider-image .-classList (.remove "active"))))))

(js/window.addEventListener "scroll" (debounce check-slide))

(defn init []
  (js/console.log "init"))
