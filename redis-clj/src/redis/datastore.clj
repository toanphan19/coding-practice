(ns redis.datastore)

(def data
  "An atom map to store all of redis's data"
  (atom {}))

(defn get-value [key]
  (get @data key))

(defn- set-val [key val] (swap! data assoc key val))
(defn set-value
  "Set value of a key. 
   Will try to convert the value into a bigint beforehand."
  [key val]
  (try
    (let [val-bigint (bigint val)]
      (set-val key val-bigint))
    (catch Exception _
      (set-val key val))))


(swap! data assoc key val)

(defn inc-value
  ([key]
   (swap! data update key inc)
   (get-value key))
  ([key amount]
   (swap! data update key (partial + amount))
   (get-value key)))


;; For reference, this version is non-thread safe:
;; (defn inc-value [key]
;;   (reset! data (update @data key inc)))


(comment
  (set-value "lang" "clojure")
  (get-value "lang")
  (set-value "tmp" 0)
  (get-value "tmp")
  (inc-value "tmp")

  (re-matches #"^[0-9]*$" "123")
  (bigint "13x"))
