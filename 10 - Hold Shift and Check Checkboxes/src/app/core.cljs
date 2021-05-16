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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Start

(def checkboxes (array-seq (js/document.querySelectorAll ".inbox input[type=\"checkbox\"]")))

(def last-checked (atom nil))

(defn filter-by-index [coll idxs]
  (keep-indexed #(when ((set idxs) %1) %2)
                coll))

(defn handle-check [e]
  (this-as this
   (when (and (-> e .-shiftKey)
              (-> this .-checked)
              @last-checked)
     (let [in-between-checkboxes (->> checkboxes
                                      (keep-indexed (fn [idx item]
                                                      (when (or (identical? this item)
                                                                (identical? @last-checked item))
                                                        idx)))
                                      (apply range)
                                      (filter-by-index checkboxes))]
       (run! #(set! (.-checked %) true) in-between-checkboxes)))
   (reset! last-checked this)))

(doseq [checkbox checkboxes]
  (.addEventListener checkbox "click" handle-check))

(defn init []
  (js/console.log "init"))