(ns app.core)

(defonce debug (atom nil))

(comment
  @debug)

(def second-hand (js/document.querySelector ".second-hand"))
(def minute-hand (js/document.querySelector ".minute-hand"))
(def hour-hand (js/document.querySelector ".hour-hand"))

(defn set-date []
  (def now (js/Date.))

  (def seconds (.getSeconds now))
  (def seconds-degrees (+ (* 360 (/ seconds 60))
                          90))
  (set! (-> second-hand .-style .-transform)
        (str "rotate(" seconds-degrees "deg)"))

  (def minutes (.getMinutes now))
  (def minutes-degrees (+ (* 360 (/ minutes 60))
                          (* 6 (/ seconds 60))
                          90))
  (set! (-> minute-hand .-style .-transform)
        (str "rotate(" minutes-degrees "deg)"))

  (def hours (.getHours now))
  (def hours-degrees (+ (* 360 (/ hours 12))
                        (* 6 (/ minutes 60))
                        90))
  (set! (-> hour-hand .-style .-transform)
        (str "rotate(" hours-degrees "deg)")))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (js/setInterval set-date 1000)
  (set-date)
  (js/console.log "start"))

(defn init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (js/console.log "init")
  (start))
