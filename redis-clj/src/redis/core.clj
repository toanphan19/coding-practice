(ns redis.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.net ServerSocket]))




(defn read-message [socket]
  ;; (with-open [reader (io/reader socket)]
  ;;   (let [msg (.readLine reader)]
  ;;     (println "Received message: " msg)
  ;;     msg)))

  ;; (let [reader (io/reader socket)
  ;;       first-char (char (.read reader))]
    ;; (case first-char
    ;;   \+ ;; simple string
    ;;   \* ;; array)

  ;; True implementation
  (let [reader (io/reader socket)]
    (loop)))

  ;; For debug
  ;; (let [reader (io/reader socket)
  ;;       msg (.readLine reader)]
  ;;   (println "Received message: " msg)
  ;;   (println "Received message: " (.readLine reader))
  ;;   (println "Received message: " (.readLine reader))
  ;;   (println "Received message: " (.readLine reader))
  ;;   (println "Received message: " (.readLine reader))
  ;;   (println "Received message: " (.readLine reader))
  ;;   (println "Received message: " (.readLine reader))
  ;;   msg))

(defn send-message
  [socket msg]
  (with-open [writer (io/writer socket)]
    (.write writer msg)))

(defn handle [msg]
  (cond
    (= msg "PING") "+PONG\r\n"
    :else "+UNKNOWN COMMAND\r\n"))

(defn serve
  [port]
  (with-open [server-socket (ServerSocket. port)
              socket (.accept server-socket)]
    (. server-socket (setReuseAddress true))
    (let [msg (read-message socket)
          response (handle msg)]
      (send-message socket response))))

(defn -main
  "Start the Redis server"
  [& args]
  (let [port 6379]
    (println "Starting server on port" port)
    (serve port)))
