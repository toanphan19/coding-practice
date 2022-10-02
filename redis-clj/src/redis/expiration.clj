(ns redis.expiration)

(def expiration-time
  "An atom map to store expiration time of keys in the db"
  (atom {}))

(defn- current-timestamp [] (System/currentTimeMillis))

(defn set-expiration-time
  "Set expiration time. 
   `timestamp` is milliseconds since epoch."
  [key timestamp] (swap! expiration-time assoc key timestamp))

(defn set-ttl
  "Set time to live of a key"
  [key seconds]
  (let [expire-timestamp (+ (current-timestamp) (* 1000 seconds))]
    (set-expiration-time key expire-timestamp)))

(defn is-expired [key]
  (if (contains? @expiration-time key)
    (< (get @expiration-time key) (current-timestamp))
    false))
(defn delete-expiration-time [key]
  (swap! expiration-time dissoc key))

(comment
  (current-timestamp)
  (set-ttl "tmp" 2)
  (is-expired "tmp")
  (delete-expiration-time "tmp"))
