(ns tic-tac-toe.core-test
  (:require [clojure.test :refer :all]
            [tic-tac-toe.core :refer :all]))

(def base-board [["X" "" ""]
                 ["O" "X" "O"]
                 ["" "" ""]])

(def board-X-winner [["X" "" ""]
                     ["O" "X" "O"]
                     ["" "" "X"]])

(def board-O-winner [["X" "" "O"]
                     ["O" "X" "O"]
                     ["" "" "O"]])

(def full-board-X-winner [["X" "X" "O"]
                          ["O" "X" "O"]
                          ["O" "O" "X"]])

(def full-board [["X" "O" "X"]
                 ["O" "O" "X"]
                 ["X" "X" "O"]])

(def base-game {:board base-board
                :state :ongoing
                :next-player "X"})

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
  (testing "Change empty position of board without changing state"
    (is (= (change-position base-game "X" [0 1])
           {:board [["X" "X" ""]
                    ["O" "X" "O"]
                    ["" "" ""]]
            :state :ongoing
            :next-player "X"})))
  (testing "Change already filled position of board throws exception"
    (is (thrown-with-msg? Exception #"Already filled position"
                          (change-position base-game "X" [1 1]))))

  (testing "Change position with a not valid player throws exception"
    (is (thrown-with-msg? Exception #"Invalid Player"
                          (change-position base-game "O" [0 1])))
    (is (thrown-with-msg? Exception #"Invalid Player"
                          (change-position base-game "banana" [0 1]))))

  (testing "Changing out of bound position throws exception"
    (is (thrown-with-msg? Exception #"Out of bounds position"
                          (change-position base-game "X" [3 1])))
    (is (thrown-with-msg? Exception #"Out of bounds position"
                          (change-position base-game "X" [1 3])))))

(deftest check-winner-for-position-test
  (testing "If the position has a winner, return the winner"
    (is (= (check-winner-for-position board-X-winner [[0 0] [1 1] [2 2]])
         "X"))
    (is (= (check-winner-for-position board-O-winner [[0 2] [1 2] [2 2]])
         "O")))
  (testing "If the position has no winner, returns nil"
    (is (= (check-winner-for-position base-board [[0 0] [0 1] [0 2]])
         nil))))


(deftest check-winner-test
  (testing "Board with winning position returns winner"
    (is (= (check-winner board-X-winner winning-positions)
           "X"))
    (is (= (check-winner board-O-winner winning-positions)
           "O")))
  (testing "Board with no winning positions returns nil as winner"
    (is (= (check-winner base-board winning-positions)
           nil))))

(deftest full-board?-test
  (testing "Full board returns true"
    (is (= (full-board? full-board)
           true)))
  (testing "Not full board returns false"
    (is (= (full-board? full-board)
           true))))


(deftest update-game-state-test
  (testing "Board without winner and not full returns ongoing game"
    (is (= (update-game-state base-game)
           (assoc base-game :next-player "O"))))
  (testing "Board with winner returns game with winner and status"
    (is (= (update-game-state {:board board-O-winner
                               :state :ongoing})
           {:board board-O-winner
            :state :finished
            :winner "O"}))
    (is (= (update-game-state {:board full-board-X-winner
                               :state :ongoing})
           {:board full-board-X-winner
            :state :finished
            :winner "X"})))
  (testing "Full board without winner returns game with draw as a winner and status"
    (is (= (update-game-state {:board full-board
                               :state :ongoing})
           {:board full-board
            :state :finished
            :winner "draw"}))))









