(ns advent-of-code-2022.day1
  (:require [clojure.string :as str]))

;; Day 1

(defn list-str->list-int [lst]
  (map #(Integer/parseInt %) lst))

(defn get-calories-per-elf []
  (let [per-elf-input (-> (slurp "data/day1_input.txt")
                          (str/split #"\n\n"))]
    (->> (map str/split-lines per-elf-input)
         (map list-str->list-int)
         (map #(reduce + %)))))

(defn day1-part1 []
  (->> (get-calories-per-elf)
       (reduce max)))

(day1-part1)

(defn day1-part2 []
  (->> (get-calories-per-elf)
       (sort)
       (take-last 3)
       (reduce +)))

(day1-part2)
