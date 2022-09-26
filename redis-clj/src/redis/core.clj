(ns redis.core
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.core.async
             :as async
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]

            [redis.datastore])
  (:import [java.net ServerSocket]))

(s/def ::msg-types #{\+ \- \: \$ \*})
;; A command is a list of arguments, such as ["ECHO" "Hello World"]
(s/def ::command coll?)
(s/def ::resp-response string?)

(defn read-and-parse-simple-string [reader]
  (.readLine reader))

(defn read-and-parse-bulk-string [reader]
  (let [num-chars  (Integer/parseInt (.readLine reader))]
    ;; TODO: Assert that the result string is of the same length
    (.readLine reader)))

(defn read-and-parse-msg [reader]
  {:post [s/valid? ::command %]}
  (let [first-char (.read reader)]
    ;; (println first-char)
    (if (neg? first-char)
      (throw (ex-info "EOF found: Client stopped the socket connection"
                      {}))
      (let [msg-type (char first-char)]
        (case msg-type
          \+ [(read-and-parse-simple-string reader)]
          \$ [(read-and-parse-bulk-string reader)]
          \* (let [num-elements (Integer/parseInt (.readLine reader))]
               (mapv first (repeatedly num-elements #(read-and-parse-msg reader))))
          "+UNKNOWN/r/n")))))

(defn send-message
  [socket msg]
  (let [writer (io/writer socket)]
    (.write writer msg)
    (.flush writer)))

;; Helpers to construct response for the Redis client
(defn response-simple-string
  "Return a RESP simple string"
  [string]
  (str "+" string "\r\n"))

(defn response-bulk-string
  "Return a RESP bulk string"
  [string]
  (response-simple-string string))

(defn response-integer
  "Return a RESP integer"
  [num]
  (str ":" num "\r\n"))

(defn response-error
  "Return a RESP error string"
  [error-msg]
  (str "-" error-msg "\r\n"))

(defn response-error-wrong-num-arguments
  [cmd-name]
  (response-error (format "ERR wrong number of arguments for '%s' command" cmd-name)))

(defn response-error-not-implemented
  [cmd-name]
  (response-error (format "ERR not implemented command '%s'" cmd-name)))

(defn response-error-unknown-command
  [cmd-name]
  (response-error (format "ERR unknown command '%s'" cmd-name)))


(defn handle
  "Handle a client command"
  [command]
  {:pre [(s/valid? coll? command)]
   :post [(s/valid? ::resp-response %)]}
  (let [cmd-name (str/upper-case (first command))
        num-args (count command)]
    (case cmd-name
      ;; "COMMAND" (case num-args
      ;;             [1 2] (response-simple-string
      ;;                    "Hi! This is a Clojure Redis server!"))
      "PING" (case num-args
               1 (response-simple-string "PONG")
               2 (response-simple-string (second command))
               (response-error-wrong-num-arguments cmd-name))
      "ECHO" (case num-args
               2 (response-simple-string (second command))
               (response-error-wrong-num-arguments cmd-name))
      "GET" (case num-args
              2 (-> (redis.datastore/get-value (second command))
                    (response-bulk-string))
              (response-error-wrong-num-arguments cmd-name))
      "SET" (case num-args
              3 (do (redis.datastore/set-value (nth command 1)
                                               (nth command 2))
                    (response-simple-string "OK"))
              (response-error-wrong-num-arguments cmd-name))
      "INCR" (case num-args
               2 (-> (redis.datastore/inc-value (second command))
                     (response-integer))
               (response-error-wrong-num-arguments cmd-name))
      "INCRBY" (case num-args
                 3 (-> (redis.datastore/inc-value (nth command 1)
                                                  (bigint (nth command 2)))
                       (response-integer))
                 (response-error-wrong-num-arguments cmd-name))

      "DEL" (response-error-not-implemented cmd-name)
      "COPY" (response-error-not-implemented cmd-name)
      "TYPE" (response-error-not-implemented cmd-name)
      "EXPIRE" (response-error-not-implemented cmd-name)
      "EXPIREAT" (response-error-not-implemented cmd-name)
      "PERSIST" (response-error-not-implemented cmd-name)
      (response-error-unknown-command cmd-name))))

(defn handle-socket [client-socket]
  (try
    (while true
      (let [reader (io/reader client-socket)
            msg (read-and-parse-msg reader)
            response (handle msg)]
        (println "Message received:" msg)
        (send-message client-socket response)))
    (catch Exception e
      (println (.getMessage e)))))

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
