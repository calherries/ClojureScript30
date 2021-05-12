(ns app.core
  (:require [goog.string :as gstr]
            [goog.string.format]))

(def debug (atom nil))
(add-tap #(reset! debug %))
;; (js/window.addEventListener "keydown" tap>)

(defn js
  "For cases when built-in js->clj doesn't work. Source: https://stackoverflow.com/a/32583549/4839573"
  [x]
  (into {} (for [k (js-keys x)] [(keyword k) (aget x k)])))

(defn play-sound [e]
  (let [key-code (.-keyCode e)
        audio-element (js/document.querySelector (gstr/format "audio[data-key=\"%s\"]" key-code))
        key-element (js/document.querySelector (gstr/format ".key[data-key=\"%s\"]" key-code))]
    (when audio-element
      (set! (.-currentTime audio-element) 0)
      (.play audio-element)
      (-> key-element .-classList (.add "playing"))
      (js/console.log key-code))))

(defn remove-transition
  [e]
  (when (= (.-propertyName e) "transform")
    (.remove (.. e -target -classList) "playing")))

;; e.target.classList.remove ('playing');

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (.addEventListener js/window "keydown" play-sound)
  (doseq [el-key (-> js/document (.querySelectorAll ".key") array-seq)]
    (-> el-key (.addEventListener "transitionend" remove-transition)))
  (js/console.log "start"))

(defn init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (js/console.log "init")
  (start))
