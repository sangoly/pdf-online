(ns pdf-online.routes.home
  (:require [compojure.core :refer :all]
            [pdf-online.views.layout :as layout]
            [noir.session :as session]
;            [hiccup.util :refer [url-encode]]
            ))

(defn home []
  (if (session/get :user)
    ;; Categoeries shouble be fill with the user's metadata
    (layout/my-render "login.html" {:categoeries ["默认分组" "c++" "java" "clojure"]})
    (layout/my-render "un_login.html" {})))

(defroutes home-routes
  (GET "/" [] (home)))
