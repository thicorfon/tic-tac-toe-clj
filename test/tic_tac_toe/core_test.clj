(ns tic-tac-toe.core-test
  (:require [clojure.test :refer :all]
            [tic-tac-toe.core :refer :all]))

(def base-board [["X" "" ""] ["O" "X" "O"] ["" "" ""]])

(def base-game {:board base-board
                :state :ongoing})

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
           base-game))))



