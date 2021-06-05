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

(def video (js/document.querySelector ".player"))
(def canvas (js/document.querySelector ".photo"))
(def ctx (.getContext canvas "2d"))
(def strip (js/document.querySelector ".strip"))
(def snap (js/document.querySelector ".snap"))

(defn get-video []
  (-> (js/navigator.mediaDevices.getUserMedia #js {:video true :audio false})
      (.then (fn [local-media-stream]
               (js/console.log local-media-stream)
               (set! (.-srcObject video) local-media-stream)
               (.play video)))
      (.catch (fn [err]
                (js/console.error "OH NO!!!" err)))))

(defn paint-to-canvas []
  (let [{width :videoWidth
         height :videoHeight} (->clj video)]
    (set! (.-width canvas) width)
    (set! (.-height canvas) height)
    (js/console.log width height)
    (js/setInterval
     (fn []
       (.drawImage ctx video 0 0 width height))
     16)))

(defn ^:export takePhoto []
  (let [data (.toDataURL canvas "image/jpeg")
        link (.createElement js/document "a")]
    (set! (.-currentTime snap) 0)
    (.play snap)
    (set! (.-href link) data)
    (.setAttribute link "download" "handsome")
    (set! (.-innerHTML link) (str "<img src=" data " alt=\"Handsome man\" />"))
    (.insertBefore strip link (.-firstChild strip))))

(get-video)

(.addEventListener video "canplay" paint-to-canvas)