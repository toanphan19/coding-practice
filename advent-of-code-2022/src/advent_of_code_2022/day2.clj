(ns advent-of-code-2022.day2
  (:require [clojure.string :as str]))

;; Part 1

(def rps-score {"X" 1, "Y" 2, "Z" 3, "A" 1, "B" 2, "C" 3})

(defn read-input-day2 []
  (->> (slurp "data/day2_input.txt")
       (str/split-lines)
       (map #(str/split % #"\s"))))

(defn calculate-total-score-of-shapes [your-choices]
  (->> (map #(get rps-score %) your-choices)
       (reduce +)))

(defn- calculate-score-of-outcome [their-choice your-choice]
  (if (or (and (= their-choice "A") (= your-choice "X"))
          (and (= their-choice "B") (= your-choice "Y"))
          (and (= their-choice "C") (= your-choice "Z")))
    3
    (if (or (and (= their-choice "A") (= your-choice "Y"))
            (and (= their-choice "B") (= your-choice "Z"))
            (and (= their-choice "C") (= your-choice "X")))
      6
      0)))

(defn calculate-total-score-of-outcome [choices]
  (->> (map #(apply calculate-score-of-outcome %) choices)
       (apply +)))

(defn calculate-total-score []
  (let [choices (read-input-day2)
        your-choices (map last choices)]
    (+ (calculate-total-score-of-shapes your-choices) (calculate-total-score-of-outcome choices))))

(calculate-total-score)

(comment
  (calculate-score-of-outcome "A" "X")

  (let [choices (read-input-day2)]
    (calculate-total-score-of-outcome choices)))

(map #(get rps-score %) ["X" "Y" "X"])



;; Part 2


(defn suggested-choice [their-choice suggestion]
  (case suggestion
    "Y" their-choice
    "X" (case their-choice
          "A" "C"
          "B" "A"
          "C" "B")
    (case their-choice
      "A" "B"
      "B" "C"
      "C" "A")))

(def outcome-scores {"X" 0, "Y" 3, "Z" 6})

(defn calculate-total-score-of-outcome-2 [choices]
  (let [outcomes (map last choices)]
    (->> (map #(get outcome-scores %) outcomes)
         (apply +))))

(defn calculate-total-score-2 []
  (let [choices (read-input-day2)
        your-choices (map #(apply suggested-choice %) choices)]
    (+ (calculate-total-score-of-outcome-2 choices)
       (calculate-total-score-of-shapes your-choices))))

(calculate-total-score-2)

(comment
  (suggested-choice "A" "X")

  (def choices (read-input-day2))
  (def your-choices (map #(apply suggested-choice %) choices))
  your-choices
  (calculate-total-score-of-shapes your-choices)

  (calculate-total-score-of-shapes '("A" "B" "C"))

  (calculate-total-score-of-outcome-2 choices)

  (get rps-score "1")
  (apply + (map #(get rps-score %) '("A" "B" "1"))))

