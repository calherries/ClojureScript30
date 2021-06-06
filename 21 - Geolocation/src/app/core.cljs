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

(def arrow (js/document.querySelector ".arrow"))

(def speed (js/document.querySelector ".speed-value"))

(.watchPosition js/navigator.geolocation
                (fn [data]
                  (js/console.log (->clj data))
                  (set! (.-textContent speed) (-> data .-coords .-speed))
                  (set! (-> arrow .-style .-transform) (str "rotate(" (-> data .-coords .-heading) "deg)")))
                (fn [error]
                  (js/console.log error)))