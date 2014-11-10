(ns pdf-online.routes.user
  (:require [compojure.core :refer [defroutes GET POST]]
            [noir.util.route :refer [restricted]]
            [noir.response :as noir-resp]
            [noir.session :as session]
            [pdf-online.models.db :as db]
            [pdf-online.views.layout :as layout]
            [pdf-online.util :as util]
            [clojure.string :refer [blank? trim]]))

(def allowed-orderby ["timestamp" "clicktimes"])

(defn valid-request? [str rules]
  (some #(= (.toLowerCase str) %) rules))

(defn favorite-exist? [pdf cur-favorite]
  (some #(and (= (:userid pdf) (:pdfowner %))
              (= (:categoery pdf) (:pdfcategoery %))
              (= (:name pdf) (:pdfname %))) 
        cur-favorite))

(defn make-favorite [pdfItems]
  (if (session/get :user)
    (let [cur-favorite (db/get-favorites-by-userid (session/get :user))]
      (map #(if (favorite-exist? % cur-favorite) (assoc % :favorite "favo") %) pdfItems))
    pdfItems))

(defn get-login-argument [userid categoery orderby]
  (let [current-user (session/get :user)
        argument-dict {:categoeries (db/get-user-categoeries current-user)
                       :refUser (db/get-user userid)
                       :refUserCategoeries (db/get-user-categoeries userid)
                       :currCate categoery
                       :pdfItems (make-favorite (db/get-pdfs-by-owner userid categoery orderby))}]
    (if (and current-user (= current-user userid))
      (assoc argument-dict :currentuserjs "currentuser.js")
      argument-dict)))

(defn get-unlogin-argument [userid categoery orderby]
  {:categoeries (db/get-user-categoeries userid)
   :refUser (db/get-user userid)
   :refUserCategoeries (db/get-user-categoeries userid)
   :currCate categoery
   :pdfItems (db/get-pdfs-by-owner userid categoery orderby)})

(defn handle-user-page-request [userid categoery orderby]
  (if (session/get :user)
    (layout/my-render "login_user_page.html" (get-login-argument userid categoery orderby))
    (layout/my-render "unlogin_user_page.html" (get-unlogin-argument userid categoery orderby))))

(defn handle-update-status-request [new-status]
  (if (blank? (trim new-status))
    (noir-resp/json {:status "default" :contents util/default-statewords})
    (if (> (count (trim new-status)) 100)
      (noir-resp/json {:status "fail" :contents "最长只能输入100字"})
      (try
        (db/update-statusword (session/get :user) {:statewords new-status})
        (noir-resp/json {:status "ok"})
        (catch Exception ex
          (noir-resp/json {:status "fail" :contents (.getMessage ex)}))))))

(defn handle-user-page-request-order [userid categoery order-by]
  (if (valid-request? order-by allowed-orderby)
    (handle-user-page-request userid categoery order-by)
    nil))

(defn handle-users-page-request [] 
  (let [users (db/get-users)]
    (if (session/get :user)
      (layout/my-render "login_users_page.html" {:users users})
      (layout/my-render "unlogin_users_page.html" {:users users}))))

(defn handle-favorite-page-request []
  (layout/my-render "my_favorite.html" {:pdfItems 
                                        (db/get-favorites-by-userid (session/get :user))}))

(defroutes user-routes
  (GET "/user/status/update" [newStatusWords]
        (restricted (handle-update-status-request newStatusWords)))
  (GET "/user/:userid" [userid]
       (handle-user-page-request-order userid "all" "clicktimes"))
  (GET "/user/:userid/:categoery" [userid categoery orderby]
       (handle-user-page-request-order userid categoery orderby))
  (GET "/users" []
       (handle-users-page-request))
  (GET "/favorite" []
       (restricted (handle-favorite-page-request))))