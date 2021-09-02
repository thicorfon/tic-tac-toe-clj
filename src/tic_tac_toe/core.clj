(ns tic-tac-toe.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]))

(defn new-board []
   [["","",""],
    ["","",""],
    ["","",""]])

(defn new-game []
  {:board (new-board)
   :state :ongoing})

(defn get-game [context]
  {:body (new-game)
   :status 200})











(def routes
  (route/expand-routes
    #{["/game" :get [http/json-body get-game] :route-name :get-game]}))


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


