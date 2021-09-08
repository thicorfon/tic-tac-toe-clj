(ns tic-tac-toe.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]))

;; valid player, alternating players, cant play where it has already been played, pretty print

(def new-board
  [["" "" ""]
   ["" "" ""]
   ["" "" ""]])


(def new-game
  {:board new-board
   :state :ongoing})

(def winning-positions #{[[0 0] [0 1] [0 2]]
                         [[1 0] [1 1] [1 2]]
                         [[0 0] [1 0] [2 0]]
                         [[2 0] [2 1] [2 2]]
                         [[0 1] [1 1] [2 1]]
                         [[0 2] [1 2] [2 2]]
                         [[0 0] [1 1] [2 2]]
                         [[0 2] [1 1] [2 0]]})

(def players #{"X" "O"})

(defonce current-game (atom new-game))


(defn get-game [_context]
  {:body   @current-game
   :status 200})

(defn reset-game! [context]
  (reset! current-game new-game)
  (get-game context))

;; pure logic
(defn get-marker [board position]
  (get-in board position))

;; pure logic
(defn get-markers [board positions]
  (map (partial get-marker board) positions))

;; pure logic
(defn change-position [game player position]
  (let [{:keys [board state]} game]
    (if (get-marker board position)
      {:board (assoc-in board position player)
       :state state}
      game)))


;; pure logic
(defn check-winner-for-position [board winning-position]
  (let [markers (get-markers board winning-position)]
    (cond (= markers ["X" "X" "X"]) "X"
          (= markers ["O" "O" "O"]) "O"
          :else nil)))


;; pure logic
(defn check-winner [board winning-positions]
  (->> (map (partial check-winner-for-position board) winning-positions)
       (some #(or % nil))))


;; pure logic
(defn full-board? [board]
  (not (some #{""} (flatten board))))


;; pure logic
(defn update-game-state [game]
  (let [{:keys [board]} game]
    (if-let [winner (check-winner board winning-positions)]
      (merge game {:state  :finished
                   :winner winner})
      (if (full-board? board)
        (merge game {:state :finished :winner "draw"})
        game))))


(defn update-board! [player position]
  (swap! current-game change-position player position)
  (swap! current-game update-game-state))


(defn make-move! [context]
  (let [{:keys [player position]} (:json-params context)]
    (update-board! player position))
  (get-game context))


(def routes
  (route/expand-routes
    #{["/game" :get [http/json-body get-game] :route-name :get-game]
      ["/game" :delete [http/json-body reset-game!] :route-name :reset-game]
      ["/game/move" :post [(body-params/body-params) http/json-body make-move!] :route-name :make-move]}))


(def service-map
  {::http/routes routes
   ::http/type   :jetty
   ::http/port   8891})


(defn start []
  (http/start (http/create-server service-map)))

;; For interactive development
(defonce server (atom nil))

(defn start-dev []
  (reset! server
          (http/start (http/create-server
                        (assoc service-map
                          ::http/join? false)))))

(defn stop-dev []
  (http/stop @server))

(defn restart []
  (stop-dev)
  (start-dev))


