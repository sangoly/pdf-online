(ns pdf-online.routes.home
  (:require [compojure.core :refer :all]
            [pdf-online.views.layout :as layout]
            [pdf-online.models.db :as db]
            [pdf-online.util :as util]
            [noir.session :as session]))

(defn home []
  (if (session/get :user)
    ;; Categoeries shouble be fill with the user's metadata
	  (layout/my-render "login_index.html" {:categoeries (db/get-user-categoeries (session/get :user))
	                                        :pdfItems (db/get-pdfs "clicktimes")})
	  (layout/my-render "index.html" {:newpdfs (db/get-pdfs "timestamp" "20")})))

(defroutes home-routes
  (GET "/" [] (home)))
