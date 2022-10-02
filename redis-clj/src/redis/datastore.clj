(ns redis.datastore
  (:require [redis.expiration :as exp]))

(def data
  "An atom map to store all of redis's data"
  (atom {}))


(defn flush-db
  "Delete all keys from the database."
  []
  (reset! data {}))

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


;; For reference, this version is non-thread safe:
;; (defn inc-value [key]
;;   (reset! data (update @data key inc)))

(defn- delete-key [key]
  (swap! data dissoc key))

(defn delete-keys
  [keys]
  (let [keys-to-delete (filter (partial contains? @data) keys)]
    (println keys keys-to-delete)
    (doseq [k keys]
      (delete-key k))
    (count keys-to-delete)))

(defn try-expire
  "Try to expire a key"
  [key]
  (when (exp/is-expired key)
    (delete-key key)
    (exp/delete-expiration-time key)))

(defn exist-keys
  [keys]
  (run! try-expire keys)
  (->> keys
       (filter (partial contains? @data))
       (count)))


(defn get-value
  [key]
  (try-expire key)
  (get @data key))

(defn inc-value
  ([key]
   (swap! data update key inc)
   (get-value key))
  ([key amount]
   (swap! data update key (partial + amount))
   (get-value key)))


(defn copy
  "Copy a value to another key."
  [source destination]
  (set-value destination (get-value source)))

(defn set-ttl
  "Set time to live of a key"
  [key seconds]
  (exp/set-ttl key seconds))

(defn set-expire-at
  [key timestamp-seconds]
  (exp/set-expiration-time key (* 1000 timestamp-seconds)))


(comment
  @data
  (set-value "lang" "clojure")
  (get-value "lang")
  (set-value "tmp" 0)
  (get-value "tmp")
  (inc-value "tmp")

  (set-ttl "tmp" 1)
  (exp/is-expired "tmp")
  (try-expire "tmp")
  (exist-keys ["tmp"])

  (re-matches #"^[0-9]*$" "123")
  (bigint "13x"))
