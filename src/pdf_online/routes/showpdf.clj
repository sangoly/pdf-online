(ns pdf-online.routes.showpdf
  (:require [compojure.core :refer [defroutes GET POST]]
            [pdf-online.util :as util]
            [pdf-online.models.db :as db]
            [pdf-online.views.layout :as layout]
            [ring.util.response :as resp]
            [noir.session :as session]
            [noir.validation :as vali]
            [noir.response :as noir-resp]
            [clojure.string :refer [blank? trim]]))

(defn hand-user-cate-name-request [userid categoery name]
  (try
    (db/update-pdf-attr userid categoery name 
                        {:clicktimes
                         (+ (:clicktimes (db/get-pdf-by-inden userid categoery name)) 1)})
    (catch Exception e
      (println "update clicktime error")))
  (resp/file-response (util/join-path-parts userid util/pdf categoery name)))

(defn hand-detail-page-request [userid categoery name]
  (let [pdf (db/get-pdf-by-inden userid categoery name)
        comments (db/get-pdf-comments userid categoery name)]
    (if (session/get :user)
     (layout/my-render "login_pdf_detail.html" {:pdf pdf :comments comments})
     (layout/my-render "unlogin_pdf_detail.html" {:pdf pdf :comments comments}))))

(defn valid-add-comment-request? [to content]
  ;; Reserve the "to" validation
  (if-not (nil? to)
    (vali/rule (= (session/get :user) to)
              [:user {:contents "不能回复自己"}]))
  (vali/rule (not (blank? (trim content)))
             [:content {:contents "回复内容不能为空"}])
  (vali/rule (< (count (trim content)) util/comment-length)
             [:length {:contents "回复内容长度不能超过500"}])
  (not (vali/errors? :user :content :length)))

(defn get-json-response [] 
  (noir-resp/json (assoc (first (vali/get-errors)) :status "warning")))

(defn hand-add-comment-request [to owner categoery name content]
  (if (valid-add-comment-request? to content)
    (try
      (db/create-comment-record {:pdfowner owner :pdfcategoery categoery
                                 :pdfname name :fromuser (session/get :user)
                                 :touser to, :content (trim content)})
      (noir-resp/json {:status "ok" :fromuser (session/get :user)
						           :content (trim content) :timestamp "刚刚"})
      (catch Exception ex
        (noir-resp/json {:status "error" :contents (.getMessage ex)})))
    (get-json-response)))

(defroutes showpdf-routes
  (GET "/read/:userid/:categoery/:name" [userid categoery name]
       (hand-user-cate-name-request userid categoery name))
  (GET "/detail/:userid/:categoery/:name" [userid categoery name]
       (hand-detail-page-request userid categoery name))
  (POST "/addComment" [to owner categoery name content]
        (if (session/get :user) 
          (hand-add-comment-request to owner categoery name content)
          (noir-resp/json {:status "unlogin"}))))