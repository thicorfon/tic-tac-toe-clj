(ns tic-tac-toe.core-test
  (:require [clojure.test :refer :all]
            [tic-tac-toe.core :refer :all]))

(def base-board [["X" "" ""] ["O" "X" "O"] ["" "" ""]])

(def board-X-winner [["X" "" ""] ["O" "X" "O"] ["" "" "X"]])

(def board-O-winner [["X" "" "O"] ["O" "X" "O"] ["" "" "O"]])

(def base-game {:board base-board
                :state :ongoing})

(deftest get-marker-test
  (testing "Get marker in valid position"
    (is (= (get-marker base-board [0 0])
           "X"))
    (is (= (get-marker base-board [0 1])
           ""))
    (is (= (get-marker base-board [1 0])
           "O")))
  (testing "Get marker in invalid position returns nil"
    (is (= (get-marker base-board [3 3])
           nil))))

(deftest get-markers-test
  (testing "Get markers from list of positions"
    (is (= (get-markers base-board [[0 0] [0 1] [3 3]])
           ["X" "" nil]))))


(deftest change-position-test
  (testing "Change valid position of board without changing state"
    (is (= (change-position base-game "X" [0 1])
           {:board [["X" "X" ""] ["O" "X" "O"] ["" "" ""]]
            :state :ongoing}))
    (is (= (change-position base-game "O" [1 1])
           {:board [["X" "" ""] ["O" "O" "O"] ["" "" ""]]
            :state :ongoing})))
  (testing "Changing out of bound position returns original game"
    (is (= (change-position base-game "X" [3 1])
           base-game))
    (is (= (change-position base-game "O" [1 3])
           base-game))))

(deftest check-winner-for-position-test
  (testing "If the position has a winner, return the winner"
    (is (= (check-winner-for-position board-X-winner [[0 0] [1 1] [2 2]])
         "X"))
    (is (= (check-winner-for-position board-O-winner [[0 2] [1 2] [2 2]])
         "O")))
  (testing "If the position has no winner, returns nil"
    (is (= (check-winner-for-position base-board [[0 0] [0 1] [0 2]])
         nil))))


(deftest check-winner-test)

(deftest full-board?-test)

(deftest update-game-state-test)



