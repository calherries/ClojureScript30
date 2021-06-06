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

(set! js/window.SpeechRecognition
      (or js/window.SpeechRecognition
          js/window.webkitSpeechRecognition))

(def recognition (js/SpeechRecognition.))

(set! (.-interimResults recognition) true)

(set! (.-lang recognition) "en-US")

(def p (js/document.createElement "p"))

(def words (js/document.querySelector ".words"))

(.appendChild words p)

(defn handle-results [e]
  (let [transcript (->> e
                        .-results
                        array-seq
                        (map (fn [results]
                               (.-transcript (aget results 0))))
                        (apply str))
        poop-script (clojure.string/replace transcript #"(?i)poop|poo|shit|dump" "💩")]
    (set! (.-textContent p) poop-script)
    (when (-> e .-results (aget 0) .-isFinal)
      (let [p (js/document.createElement "p")]
        (.appendChild words p)))))

(.addEventListener recognition "result" handle-results)

(.addEventListener recognition "end" (.-start recognition))

(.start recognition)
