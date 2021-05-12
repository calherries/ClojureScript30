(ns app.core)

(defonce debug (atom nil))
(add-tap #(reset! debug %))

(comment
  @debug)

(def inputs (array-seq (js/document.querySelectorAll ".controls input")))

(defn js
  "For cases when built-in js->clj doesn't work. Source: https://stackoverflow.com/a/32583549/4839573"
  [x]
  (into {} (for [k (js-keys x)]
             [(keyword k) (aget x k)])))

(defn handle-update [e]
  (let [this (.-target e)
        suffix (or (-> this .-dataset .-sizing)
                   "")
        css-variable (str "--" (.-name this))
        css-value (str (.-value this) suffix)]
    (-> js/document.documentElement.style (.setProperty css-variable css-value))))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (doseq [input inputs]
    (-> input (.addEventListener "mouseover" handle-update))
    (-> input (.addEventListener "change" #(do (tap> %)
  (js/console.log "start"))

(defn init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (js/console.log "init")
  (start))
