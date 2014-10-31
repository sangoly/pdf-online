(ns pdf-online.routes.home
  (:require [compojure.core :refer :all]
            [pdf-online.views.layout :as layout]
            [pdf-online.models.db :as db]
            [pdf-online.util :as util]
            [noir.session :as session]))

(defn home []
  (if (session/get :user)
 ;; Categoeries shouble be fill with the user's metadata
 (layout/my-render "login.html" {:categoeries ["默认分组" "c++" "java" "clojure"]
                                 :pdfItems (db/get-pdfs)})
 (layout/my-render "un_login.html" {})))

(defroutes home-routes
  (GET "/" [] (home)))
