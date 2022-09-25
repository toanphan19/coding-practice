(ns redis.datastore-test
  (:require [clojure.core.async :refer [go]]
            [clojure.test :refer :all]
            [redis.datastore :refer :all]))

(deftest get-set-test
  (testing "Test get and set data"
    (set-value "lang" "clojure")
    (is (= (get-value "lang") "clojure"))
    (set-value "num" "10")
    (is (= (get-value "num") 10))))


(deftest inc-value-test
  (testing "Test increase value of a key"
    (set-value "counter" "10")
    (doseq [_ (range 1000)]
      (inc-value "counter"))
    (is (= (get-value "counter") 1010)))

  (testing (str "Test increase value in multiple threaded."
                "NOTE: Very naive implementation with Thread/sleep")
    (set-value "counter" "10")
    (doseq [_ (range 100)]
      (go (doseq [_ (range 1000)]
            (inc-value "counter"))))
    (Thread/sleep 200)
    (is (= (get-value "counter") 100010))))

(comment
  (def x {})

  (get (assoc x "name" "Toan") "name")
  (:name (assoc x "name" "Toan")))