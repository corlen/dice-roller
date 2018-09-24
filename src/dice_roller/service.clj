(ns dice-roller.service
  (:require [io.pedestal.http :as bootstrap]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [io.pedestal.interceptor.helpers :refer [definterceptor defhandler]]
            [ring.util.response :as ring-resp]))


(defn custom-rand-int [die-face] (int (/ (+ (rand-int die-face) (rand-int die-face)) 2)))
(defn roll [rolls die-face] (map inc(repeatedly rolls #(custom-rand-int die-face))))

(defn get-rolls
  [request]
   (let [incoming (:json-params request)]
    (let [die (:die incoming)
          rolls (:rolls incoming)]
  (bootstrap/json-response {:values (roll rolls die)}))))

(defn home-page
  [request]
  (ring-resp/not-found "Sorry! No donut for you :("))

(defroutes routes
  ;; Defines "/" and "/about" routes with their associated :get handlers.
  ;; The interceptors defined after the verb map (e.g., {:get home-page}
  ;; apply to / and its children (/about).
  [[["/" {:get home-page}
     ^:interceptors [(body-params/body-params) bootstrap/html-body]
     ["/roll" {:post get-rolls}]]]])

;; Consumed by dice-roller.server/create-server
;; See bootstrap/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::bootstrap/interceptors []
              ::bootstrap/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::bootstrap/allowed-origins ["scheme://host:port"]

              ;; Root for resource interceptor that is available by default.
              ::bootstrap/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ::bootstrap/type :jetty
              ;;::bootstrap/host "localhost"
              ;;::bootstrap/port 8080})
              ::bootstrap/port (Integer. (or (System/getenv "PORT") 8080))})


