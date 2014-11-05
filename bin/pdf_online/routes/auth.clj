(ns pdf-online.routes.auth
  (:require [compojure.core :refer [defroutes POST GET ANY]]
            [noir.util.crypt :as crypt]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.route :refer [restricted]]
            [pdf-online.models.db :as db]
            [pdf-online.views.layout :as layout]
            [pdf-online.util :as util]
            [ring.util.response :as ring-resp])
  (:import [java.io File]))

(def valid-image-extends ["jpeg" "png" "gif"])


;; If find then return ture or nil
(defn valid-extend? [str rules]
  (some #(= (.toLowerCase (last (clojure.string/split str #"/"))) %) 
        rules))

(defn valid-register? [{:keys [size content-type filename]} username pass pass1]
  (if-not (empty? filename)
    (do
      (vali/rule (< size (* 2 1024 1024)) 
                [:size "图片尺寸过大，请选择小于2M的图片"])
      (vali/rule (valid-extend? content-type valid-image-extends) 
                 [:type "只能选择jpeg、png、gif格式图片"])))
  (vali/rule (vali/has-value? username)
             [:username "请输入用户名"])
  (vali/rule (vali/has-value? pass)
             [:pass "请输入密码"])
  (vali/rule (vali/has-value? pass1)
             [:pass1 "请输入确认密码"])
  (vali/rule (vali/min-length? username 6)
             [:username "用户名不能少于6位"])
  (vali/rule (vali/min-length? pass 8)
             [:pass "密码不能少于8位"])
  (vali/rule (= pass pass1)
             [:pass "两次密码不一致"])
  (not (vali/errors? :username :pass :pass1 :size :type)))

(defn format-error [id ex]
  (cond 
    (and (instance? org.postgresql.util.PSQLException ex)
         (= 0 (.getErrorCode ex)))
    (str "ID为" id " 的用户已存在")
    :else
    "创建用户出错"))

(defn headimage-path [{:keys [filename]}]
  (if-not (empty? filename)
    filename
    ""))

;; The operation of delete a user
(defn delete-account [username]
  (try
    (db/delete-user username)
    (let [user-root-path (util/join-path-parts username)]
      (if (.exists (File. user-root-path))
        (util/delete-directory user-root-path)))
    (catch Exception ex
      (println (.getMessage ex)))))
;      (session/put! :errors "删除用户出错")
;      (layout/my-render "login.html" {})

;; Handle the registration intent
(defn hand-registration [headimage username pass pass1]
  (if (valid-register? headimage username pass pass1)
    (try
      (db/create-user {:id username, :pass (crypt/encrypt pass),
                       :superuser false, :statewords util/default-statewords,
                       :image (headimage-path headimage)})
      (db/create-categoery-record {:userid username :categoery "default"})
      (util/create-new-user-folder username)
      (if-not (empty? (:filename headimage))
        (util/save-upload-file headimage (util/join-path-parts username util/image)))
      (session/put! :user username)
      (resp/redirect "/")
      (catch Exception ex
        (vali/rule false [:id (format-error username ex)])
        (session/put! :errors (vali/get-errors))
        (resp/redirect "/")))
    (do 
      (session/put! :errors (vali/get-errors))
      (resp/redirect "/"))))


;; Hand the login logout flow
(defn handle-login [username pass]
  (let [user (db/get-user username)]
    (if-not user
      (session/put! :errors ["用户不存在"])
      (if-not (crypt/compare pass (:pass user))
        (session/put! :errors ["密码不正确"])
        (session/put! :user username)))
    (resp/redirect "/")))

(defn handle-logout []
  (session/clear!)
  (resp/redirect "/"))

(defn serve-head-image [username]
  (let [image (:image (db/get-user username))]
    (if (empty? image)
      (ring-resp/file-response (util/join-path-parts "default" "default_headimage.jpg"))
      (ring-resp/file-response (util/join-path-parts username util/image image)))))

;; The routes for auth 
(defroutes auth-routes
  (POST "/register" [headimage username pass pass1]
        (hand-registration headimage username pass pass1))        
  (POST "/login" [username pass] (handle-login username pass))
  (GET "/logout" [] (handle-logout))
  (GET "/:username/head-image" [username] (serve-head-image username)))