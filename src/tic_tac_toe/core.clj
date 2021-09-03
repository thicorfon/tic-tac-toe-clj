(ns tic-tac-toe.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]))



(def new-board
   [["","",""],
    ["","",""],
    ["","",""]])

(def new-game
  {:board new-board
   :state :ongoing})

(defonce current-game (atom new-game))


(defn get-game [_context]
  {:body @current-game
   :status 200})

(defn reset-game! [context]
  (reset! current-game (new-game))
  (get-game context))

(defn change-position [game player position]
  {:board (assoc-in (:board game) position player)
   :state :ongoing})

(defn check-winner [board]
  "X")

(defn update-game-state [game]
  (let [{:keys [board]} game]
    (if-let [winner (check-winner board)] (merge game {:state :finished :winner winner}) game)))


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


