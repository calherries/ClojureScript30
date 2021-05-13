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

(def dogs
  [{:name "Snickers"
    :age  2}
   {:name "hugo"
    :age  8}])

(defn makeGreen
  []
  (let [p (.querySelector js/document "p")]
    (set! (.. p -style -color) "#BADA55")
    (set! (.. p -style -fontSize) "50px")))

(log "hello")

(log (str "Hello I am " "poop" " string!"))

(js/console.warn "Oh nooo")

(js/console.error "Shit!")

(js/console.info "Did you know...")

(def p (js/document.querySelector "p"))

(js/console.assert (-> p .-classList (.contains "ouch"))
                   "That is wrong!")

(js/console.clear)

(js/console.log p)

(js/console.dir p)

(doseq [dog dogs]
  (js/console.group (:names dog))
  (log (str "This is " (:name dog)))
  (log (str (:hame dog) " is " (:age dog) " years old"))
  (js/console.groupEnd (:name dog)))

(js/console.count "Wes")
(js/console.count "Steve")
(js/console.count "Steve")
(js/console.count "Wes")
(js/console.count "Wes")
(js/console.count "Wes")
(js/console.count "Steve")
(js/console.count "Steve")
(js/console.count "Wes")
(js/console.count "Wes")

(js/console.time "fetching data")
(-> (js/fetch "https://api.github.com/users/wesbos")
    (.then (fn [data] (.json data)))
    (.then
     (fn [data]
       (do
         (js/console.timeEnd "fetching data")
         (log data)))))

(js/console.table (clj->js dogs))

(defn init []
  (js/console.log "init"))