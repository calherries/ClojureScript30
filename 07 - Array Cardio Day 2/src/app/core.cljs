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

(def people
  [{:name "Wes"
    :year 1988}
   {:name "Kait"
    :year 1986}
   {:name "Irv"
    :year 1970}
   {:name "Lux"
    :year 2015}])

(def comments
  [{:text "Love this!"
    :id   523423}
   {:text "Super good"
    :id   823423}
   {:text "You are the best"
    :id   2039842}
   {:text "Ramen is my fav food ever"
    :id   123523}
   {:text "Nice Nice Nice!"
    :id   542328}])

;; // Some and Every Checks
;; // Array.prototype.some() // is at least one person 19 or older?
;; // Array.prototype.every() // is everyone 19 or older?

(def current-year 2021)

(defn current-age
  [born-year]
  (- current-year born-year))

;; some
(->> people
     (some #(< 19 (current-age (:year %)))))

;; every
(->> people
     (every? #(< 19 (current-age (:year %)))))

;; // Array.prototype.find()
;; // Find is like filter, but instead returns just the one you are looking for
;; // find the comment with the ID of 823423
(->> comments
     (filter (comp #{823423} :id))
     first)

;; // Array.prototype.findIndex()
;; // Find the comment with this ID
;; // delete the comment with the ID of 823423
(->> comments
     (remove (comp #{823423} :id)))
