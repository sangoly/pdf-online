(ns pdf-online.routes.upload
  (:require [compojure.core :refer [defroutes POST]]
            [pdf-online.util :as util]
            [pdf-online.models.db :as db]
            [pdf-online.views.layout :as layout]
            [pdf-online.routes.auth :refer [valid-extend?]]
            [noir.util.route :refer [restricted]]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [ring.util.codec :as codec]))

(def valid-file-extends ["pdf"])

(defn valid-file? [filename content-type categoery]
  (vali/rule (not (empty? filename))
             [:file "没有选择要上传的文件"])
  (vali/rule (valid-extend? content-type valid-file-extends)
             [:type "必需上传PDF文件"])
  (vali/rule (not (empty? categoery))
             [:categoery "分组信息不能空"])
  (not (vali/errors? :file :type :categoery)))

(defn hand-upload-file 
  [{:keys [filename content-type] :as file} categoery introduce]
  (if (valid-file? filename content-type categoery)
    (if (nil? (db/get-pdf-by-inden (session/get :user) categoery filename))
	    (try
	      (db/create-pdf-record {:userid (session/get :user) :categoery categoery
	                             :name filename :introduce introduce})
	      (util/save-upload-file 
	        file 
	        (util/join-path-parts (session/get :user) util/pdf categoery))
        (db/update-pdfCategoeries-count (session/get :user) categoery
                                        {:count (+ 1 (:count (db/get-pdfCategoeries-count 
                                                              (session/get :user) categoery)))})
	      (catch Exception e
	        (db/delete-pdf-record (session/get :user) categoery filename)
	        (session/put! :errors ["发生错误，上传文件失败"])))
      (session/put! :errors ["在相同类别下已存在同名文件"]))
    (session/put! :errors (vali/get-errors)))
  (resp/redirect "/"))

(defroutes upload-routes
  (POST "/upload_file" [pdfbutn categoery introduce]
        (restricted (hand-upload-file pdfbutn categoery introduce))))