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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Globals

(def endpoint
  "https://gist.githubusercontent.com/Miserlou/c5cd8364bf9b2420bb29/raw/2bf258763cdddd704f8ffd3ea9a3e81d25e2c6f6/cities.json")

(def cities (atom nil))

(def suggestions (js/document.querySelector ".suggestions"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Helpers

(defn find-matches
  "Assumes cities is a cljs map"
  [word-to-match cities]
  (->> cities
       (filter #(or (re-find (re-pattern word-to-match) (:city %))
                    (re-find (re-pattern word-to-match) (:state %))))))

(defn number-with-commas
  [x]
  (clojure.string/replace (str 100000) #"\B(?=(\d{3})+(?!\d))" ","))

(defn highlighted-result
  [search-string {:keys [city state population]}]
  (let [place-name (clojure.string/replace (str city ", " state)
                                           (re-pattern search-string)
                                           (str "<span class=\"hl\">" search-string "</span>"))]
    (str "<li>
           <span class=\"name\">" place-name "</span>
           <span class=\"population\">" (number-with-commas population) "</span>
         </li>")))

(defn highlighted-results
  [search-string cities]
  (let [match-array (find-matches search-string cities)]
    (apply array (map #(highlighted-result search-string %) match-array))))

(defn display-matches []
  (let [results-list (highlighted-results (.-value (js-this)) @cities)]
    (set! (.-innerHTML suggestions) results-list)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;  Start

(-> (js/fetch endpoint)
    (.then (fn [blob] (.json blob)))
    (.then (fn [data] (reset! cities (map cljs data)))))

(let [search-input (js/document.querySelector ".search")]
  (-> search-input (.addEventListener "change" display-matches))
  (-> search-input (.addEventListener "keyup" display-matches)))
