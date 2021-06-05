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

(def bands ["The Plot in You" "The Devil Wears Prada" "Pierce the Veil" "Norma Jean"
            "The Bled" "Say Anything" "The Midway State" "We Came as Romans" "Counterparts"
            "Oh, Sleeper" "A Skylit Drive" "Anywhere But Here" "An Old Dog"])

(defn strip [band-name]
  (-> band-name
      (str/replace #"(?i)(^a |the |an )" "")
      (str/trim)))

(def sorted-bands
  (sort-by strip compare bands))

(set! (.-innerHTML (js/document.querySelector "#bands"))
      (->> sorted-bands
           (map (fn [band] (str "<li>" band "</li>")))
           (apply str)))

(js/console.log sorted-bands)