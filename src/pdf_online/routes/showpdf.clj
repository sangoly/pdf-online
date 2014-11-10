(ns pdf-online.routes.showpdf
  (:require [compojure.core :refer [defroutes GET POST]]
            [pdf-online.util :as util]
            [pdf-online.models.db :as db]
            [pdf-online.views.layout :as layout]
            [ring.util.response :as resp]
            [noir.session :as session]
            [noir.validation :as vali]
            [noir.response :as noir-resp]
            [noir.util.route :refer [restricted]]
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
     (layout/my-render "login_pdf_detail.html" {:pdf pdf :comments comments
                                                :categoeries 
                                                (db/get-user-categoeries (session/get :user))})
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

;; Delete pdf 1、delete from pdfs table;
;;            2、update the pdfCategoeries table count
;;            3、delete the file
(defn handle-delete-pdf-request [userid categoery name]
  (let [current-user (session/get :user)]
    (if (and current-user (= current-user userid))
      (let [old-count (:count (db/get-pdfCategoeries-count userid categoery))]
        (try
          (db/delete-pdf-record userid categoery name)
          (if (> old-count 0) (db/update-pdfCategoeries-count userid categoery 
                                                              {:count (dec old-count)}))
          (util/delete-directory (util/join-path-parts userid util/pdf categoery name))
	        (noir-resp/json {:status "ok" :contents (dec old-count)})
	        (catch Exception ex
	          (noir-resp/json {:status "fail" :contents "删除PDF出错"}))))
      (noir-resp/json {:status "notowner"}))))

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

(defn handle-favorite-add-request [userid categoery name]
  (try
    (db/create-favorite-record {:userid (session/get :user) :pdfowner userid
                                :pdfcategoery categoery :pdfname name})
    (noir-resp/json {:status "ok" :contents "取消收藏"})
    (catch Exception ex
      (noir-resp/json {:status "fail" :contents "收藏发生错误"}))))

(defn handle-favorite-delete-request [userid categoery name]
  (try
    (db/delete-favorite-record (session/get :user) userid categoery name)
    (noir-resp/json {:status "ok" :contents "收藏"})
    (catch Exception ex
      (noir-resp/json {:status "fail" :contents "取消收藏发生错误"}))))

(defroutes showpdf-routes
  (GET "/read/:userid/:categoery/:name" [userid categoery name]
       (hand-user-cate-name-request userid categoery name))
  (GET "/detail/:userid/:categoery/:name" [userid categoery name]
       (hand-detail-page-request userid categoery name))
  (GET "/delete/:userid/:categoery/:name" [userid categoery name]
       (restricted (handle-delete-pdf-request userid categoery name)))
  (GET "/favorite/add" [userid categoery name]
       (restricted (handle-favorite-add-request userid categoery name)))
  (GET "/favorite/delete" [userid categoery name]
       (restricted (handle-favorite-delete-request userid categoery name)))
  (POST "/addComment" [to owner categoery name content]
        (if (session/get :user) 
          (hand-add-comment-request to owner categoery name content)
          (noir-resp/json {:status "unlogin"}))))