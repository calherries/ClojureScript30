(ns app.core)

(defonce debug (atom nil))
(add-tap #(reset! debug %))

(count @tapset)

(comment
  @debug)

(defn js
  "For cases when built-in js->clj doesn't work. Source: https://stackoverflow.com/a/32583549/4839573"
  [x]
  (into {} (for [k (js-keys x)]
             [(keyword k) (aget x k)])))

(def panels (array-seq (js/document.querySelectorAll ".panel")))

(defn toggle-open []
  (-> (js-this) .-classList (.toggle "open")))

(defn toggle-active [e]
  (js/console.log (.-propertyName e))
  (if (clojure.string/includes? "flex-content" "flex")
    (-> (js-this) .-classList (.toggle "open-active"))))

(->> panels
     (run! #(.addEventListener % "click" toggle-open)))

(->> panels
     (run! #(.addEventListener % "transitionend" toggle-active)))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (js/console.log "start"))


(defn init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (js/console.log "init")
  (start))
