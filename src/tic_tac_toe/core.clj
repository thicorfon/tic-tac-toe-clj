(ns tic-tac-toe.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]))


(def new-board
  [["" "" ""]
   ["" "" ""]
   ["" "" ""]])


(def new-game
  {:board new-board
   :state :ongoing
   :next-player "X"})

(def winning-positions #{[[0 0] [0 1] [0 2]]
                         [[1 0] [1 1] [1 2]]
                         [[0 0] [1 0] [2 0]]
                         [[2 0] [2 1] [2 2]]
                         [[0 1] [1 1] [2 1]]
                         [[0 2] [1 2] [2 2]]
                         [[0 0] [1 1] [2 2]]
                         [[0 2] [1 1] [2 0]]})

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
  (let [{:keys [board next-player]} game]
    (if (not= next-player player)
      (throw (Exception. "Invalid Player")))
    (case (get-marker board position)
      ""  (update game :board assoc-in position player)
      nil (throw (Exception. "Out of bounds position"))
      (throw (Exception. "Already filled position")))))

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
(defn next-player [current-player]
  (if (= current-player "X") "O" "X"))

;; pure logic
(defn update-game-state [game]
  (let [{:keys [board]} game]
    (if-let [winner (check-winner board winning-positions)]
      {:board  board
       :state  :finished
       :winner winner}
      (if (full-board? board)
        {:board board
         :state :finished
         :winner "draw"}
        (update game :next-player next-player)))))


(defn update-board! [player position]
  (swap! current-game #(-> (change-position % player position)
                           (update-game-state))))


(defn make-move! [context]
  (let [{:keys [player position]} (:json-params context)]
    (try
      (update-board! player position)
      (get-game context)
      (catch Exception e
        {:body   {:error-message (.getMessage e)}
         :status 400}))))



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


