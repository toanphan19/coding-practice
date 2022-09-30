(ns dummy.core
  "A dummy TCP socket server to capture messages from Redis clients.
   Useful to reverse engineer a Redis server when official documentation is lacking."
  (:require [clojure.core.async :refer [go]]
            [clojure.java.io :as io])
  (:import [java.net ServerSocket]))

(defn read-and-parse-msg [reader]
  (loop [msg nil]
    (when msg (println "Msg: " msg))
    (let [line (.readLine reader)]
      (when line (recur line)))))

(defn response-simple-string
  "Return a RESP simple string"
  [string]
  (str "+" string "\r\n"))

(defn handle-socket [client-socket]
  (while true
    (let [reader (io/reader client-socket)]
      (read-and-parse-msg reader)
      (response-simple-string "OK"))))

(defn serve
  [port]
  (with-open [server-socket (ServerSocket. port)]
    (while true
      (println "Waiting for a new client...")
      (let [client-socket (.accept server-socket)]
        (println "New client connected.")
        (go (handle-socket client-socket))))))


(defn -main
  "Start the Redis server"
  [& args]
  (let [port 6379]
    (println "Starting server on port" port)
    (serve port)))