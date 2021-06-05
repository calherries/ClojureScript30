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

(def timenodes (array-seq (js/document.querySelectorAll "[data-time]")))

(def seconds
  (->> timenodes
       (map (fn [timenode]
              (let [time-str          (-> timenode .-dataset .-time)
                    [minutes seconds] (map js/parseInt (clojure.string/split time-str #":"))]
                (+ (* 60 minutes) seconds))))
       (apply +)))

(def hours (quot seconds 3600))
(def seconds-after-hours (mod seconds 3600))

(def minutes (quot seconds-after-hours 60))
(def seconds-after-minutes (mod seconds-after-hours 60))

(js/console.log hours minutes seconds-after-minutes)