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

;; <script>
;;   const msg = new SpeechSynthesisUtterance();
;;   let voices = [];
;;   const voicesDropdown = document.querySelector('[name="voice"]');
;;   const options = document.querySelectorAll('[type="range"], [name="text"]');
;;   const speakButton = document.querySelector('#speak');
;;   const stopButton = document.querySelector('#stop');
;; </script>

(def msg (js/SpeechSynthesisUtterance.))

(def voices (atom []))

(def voices-dropdown (js/document.querySelector "[name=\"voice\"]"))

(def options (js/document.querySelectorAll "[type=\"range\"], [name=\"text\"]"))

(def speak-button (js/document.querySelector "#speak"))

(def stop-button (js/document.querySelector "#stop"))

(set! (.-text msg) (.-value (js/document.querySelector "[name=\"text\"]")))

(defn toggle [start-over?]
  (.cancel js/speechSynthesis)
  (when start-over?
    (.speak js/speechSynthesis msg)))

(defn populate-voices []
  (reset! voices (array-seq (js/speechSynthesis.getVoices)))
  (let [html   (->> @voices
                    (filter (fn [voice] (re-find #"en" (.-lang voice))))
                    (map (fn [voice] (str "<option value=\"" (.-name voice) "\">" (.-name voice)" (" (.-lang voice) ")</option>")))
                    (apply str))]
    (js/console.log html)
    (set! (.-innerHTML voices-dropdown) html)))

(defn set-voice []
  (js/console.log @voices)
  (let [name (.-value (js-this))
        voice (first (filter (fn [voice] (= (.-name voice) name))
                             @voices))]
    (goog.object/set msg "voice" voice)))

(defn set-option []
  (goog.object/set msg (.-name (js-this)) (.-value (js-this)))
  (toggle true))

(.addEventListener js/speechSynthesis "voiceschanged" populate-voices)

(.addEventListener voices-dropdown "change" set-voice)

(doseq [option options]
  (.addEventListener option "change" set-option))

(.addEventListener speak-button "click" #(toggle true))

(.addEventListener stop-button "click" #(toggle false))
