(ns pdf-online.routes.upload
  (:require [compojure.core :refer [defroutes POST]]
            [pdf-online.util :as util]
            [pdf-online.models.db :as db]
            [pdf-online.views.layout :as layout]
            [noir.util.route :refer [restricted]]
            [noir.session :as session]
            [noir.response :as resp]
            [ring.util.codec :as codec]))

(defn hand-upload-file 
  [{:keys [filename] :as file} categoery introduce]
  (if-not (empty? filename)
    (if (nil? (db/get-pdf-by-inden (session/get :user) categoery filename))
	    (try
	      (db/create-pdf-record {:userid (session/get :user) :categoery categoery
	                             :name filename :introduce introduce})
	      (util/save-upload-file 
	        file 
	        (util/join-path-parts (session/get :user) util/pdf categoery))
	      (catch Exception e
	        (db/delete-pdf-record (session/get :user categoery filename))
	        (session/put! :errors ["发生错误，上传文件失败"])))
      (session/put! :errors ["在相同类别下已存在同名文件"]))
    (session/put! :errors ["没有选择要上传的文件"]))
  (resp/redirect "/"))

(defroutes upload-routes
  (POST "/upload_file" [pdfbutn categoery introduce]
        (restricted (hand-upload-file pdfbutn categoery introduce))))