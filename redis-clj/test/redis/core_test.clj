(ns redis.core-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [redis.core :refer :all]))

(deftest test-read-and-parse-msg
  (testing "Simple read and parse test"
    (let [test-cases
          [{:input "+Hi\r\n" :expected ["Hi"]}
           {:input "$4\r\nPING\r\n" :expected ["PING"]}
           {:input "*1\r\n$4\r\nPING\r\n" :expected ["PING"]}
           {:input "*2\r\n$4\r\nECHO\r\n$3\r\nHi\r\n" :expected ["ECHO" "Hi"]}]]
      (doseq [test-case test-cases]
        (with-open [reader (io/reader (char-array (:input test-case)))]
          (is (= (read-and-parse-msg reader) (:expected test-case))))))))

    ;; (with-open [reader (io/reader (char-array "+HI\r\n"))]
    ;;   (is (= (read-and-parse-msg reader) ["HI"])))
    ;; (with-open [reader (io/reader (char-array "$4\r\nPING\r\n"))]
    ;;   (is (= (read-and-parse-msg reader) ["PING"])))
    ;; (with-open [reader (io/reader (char-array "*1\r\n$4\r\nPING\r\n"))]
    ;;   (is (= (read-and-parse-msg reader) ["PING"])))
    ;; (with-open [reader (io/reader
    ;;                     (char-array "*2\r\n$4\r\nECHO\r\n$3\r\nHi\r\n"))]
    ;;   (is (= (read-and-parse-msg reader) ["ECHO" "Hi"])))))
