(ns app.core
  (:require [lambdaisland.dom-types]
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

(def add-items (js/document.querySelector ".add-items"))

(def items-list (js/document.querySelector ".plates"))

(def items
  (atom (or (-> (js/localStorage.getItem "items")
                js/JSON.parse
                (js->clj :keywordize-keys true))
            [])))

(defn items-view [items]
  (->> items
       (map-indexed (fn [idx item]
                      (str "<li>\n"
                           " <input type=\"checkbox\" data-index=" idx " id=\"item" idx "\" " (when (:done item) "checked") " />\n"
                           " <label for=\"item" idx "\">" (:text item) "</label>\n"
                           "</li>")))
       (apply str)))

(defn add-item [e]
  (.preventDefault e)
  (let [input-text (-> (js-this) (.querySelector "[name=item]") .-value)]
    (swap! items conj {:text input-text
                       :done false})
    (js/localStorage.setItem "items" (js/JSON.stringify (clj->js @items)))
    (set! (.-innerHTML items-list) (items-view @items))
    (.reset (js-this))))

(defn toggle-done [e]
  (when (-> e .-target (.matches "input"))
    (let [index (-> e .-target .-dataset .-index js/parseInt)]
      (swap! items update-in [index :done] not)
      (js/localStorage.setItem "items" (js/JSON.stringify (clj->js @items)))
      (set! (.-innerHTML items-list) (items-view @items)))))

(.addEventListener add-items "submit" add-item)
(.addEventListener items-list "click" toggle-done)

(set! (.-innerHTML items-list) (items-view @items))
