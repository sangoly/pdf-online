(ns pdf-online.routes.upload
  (:require [compojure.core :refer [defroutes POST]]
            [pdf-online.util :as util]
            [pdf-online.models.db :as db]
            [pdf-online.views.layout :as layout]
            [noir.util.route :refer [restricted]]
            [noir.session :as session]
            [noir.response :as resp]))

(defn hand-upload-file 
  [{:keys [filename] :as file} categoery introduce]
  (if-not (empty? filename)
    (util/save-upload-file file 
                           (util/join-path-parts (session/get :user) util/pdf categoery))
    (session/put! :errors ["没有选择要上传的文件"]))
  (resp/redirect "/"))

(defroutes upload-routes
  (POST "/upload_file" [pdfbutn categoery introduce]
        (restricted (hand-upload-file pdfbutn categoery introduce))))