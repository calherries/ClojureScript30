(ns app.core
  (:require [lambdaisland.dom-types]))

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
;;; WIP

(def pressed (atom []))

(def secret-code "wesbos")

(js/document.addEventListener "keyup"
                              (fn [e]
                                (js/console.log (.-key e))
                                (swap! pressed conj (.-key e))
                                (when (clojure.string/includes? (apply str @pressed) secret-code)
                                  (js/console.log "DING DING!"))))

(defn init []
  (js/console.log "init"))
