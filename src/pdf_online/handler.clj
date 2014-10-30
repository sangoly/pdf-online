(ns pdf-online.handler
  (:require [compojure.core :refer [defroutes routes]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [pdf-online.routes.home :refer [home-routes]]
            [pdf-online.routes.auth :refer [auth-routes]]
            [pdf-online.routes.upload :refer [upload-routes]]
            [pdf-online.util :refer [get-template-path]]
            [noir.util.middleware :as noir-middleware]
            [noir.session :as session]
            [selmer.parser :refer [render render-file]]
            [ring.middleware.anti-forgery :as anti-forgery]
            ;; For template error handle
            [selmer.middleware :refer [wrap-error-page]]
            [environ.core :refer [env]])
  (:import [java.io File]))

(defn init []
  (println "pdf-online is starting"))

(defn user-page [_]
  (session/get :user))

(defn destroy []
  (println "pdf-online is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found 
    (render-file (get-template-path "404.html") {})))

(def app
  (noir-middleware/app-handler 
    [upload-routes auth-routes home-routes app-routes]
    :access-rules [user-page]
    ;::middleware [anti-forgery/wrap-anti-forgery]
    ))

(#(if (env :dev) (wrap-error-page %) %) app)


