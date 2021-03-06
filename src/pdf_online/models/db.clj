(ns pdf-online.models.db
  (:require [clojure.java.jdbc :as sql]
            [pdf-online.util :as util]
            [clojure.string :refer [join]]))

(def db 
  {:subprotocol "postgresql"
   :subname "//localhost/pdfonline"
   :user "admin"
   :password "admin"
   })

(defmacro with-db [f & body]
  `(sql/with-connection ~db (~f ~@body)))

;; The user table operations
(defn create-user [user]
  (with-db sql/insert-record :users user))

(defn delete-user [id]
  (with-db sql/delete-rows :users ["id = ?" id]))

(defn get-user [id]
  (with-db sql/with-query-results
    res ["select * from users where id = ?" id] (first res)))

(defn get-users []
  (with-db sql/with-query-results
    res ["select * from users"] (doall res)))

(defn get-users []
  (with-db sql/with-query-results
    res ["select * from users limit 10"] (doall res)))

(defn update-statusword [userid param]
  (with-db sql/update-values :users
    ["id = ?" userid] param))

;; The pdfs table operations
(defn create-pdf-record [pdf-record]
  (with-db sql/insert-record :pdfs pdf-record))

(defn delete-pdf-record [username categoery name]
  (with-db sql/delete-rows :pdfs 
    ["userid = ? and categoery = ? and name = ?" username categoery name]))

(defn delete-pdf-categoery [username categoery]
  (with-db sql/delete-rows :pdfs
    ["userid = ? and categoery = ?" username categoery]))

(defn get-pdf-by-inden [username categoery name]
  (with-db sql/with-query-results
    res ["select * from pdfs where userid = ? and categoery = ? and name = ?"
         username categoery name] (first res)))

(defn get-pdfs [order-by & limit]
  (with-db sql/with-query-results
    res [(str "select * from pdfs order by " order-by " desc" (if limit 
                                                                (str " limit " (first limit))))] 
    (doall res)))

(defn get-pdfs-by-owner [userid categoery order-by]
  (with-db sql/with-query-results
    res 
    (if (= categoery "all") 
      [(str "select * from pdfs where userid = ? order by " order-by " desc") userid]
      [(str "select * from pdfs where userid = ? and categoery = ? order by " order-by " desc")
            userid categoery]) 
    (doall res)))

;; use userid categoery and name can find an only pdf record
(defn update-pdf-attr [userid categoery name param]
  (with-db sql/update-values
    :pdfs ["userid = ? and categoery = ? and name = ?"
           userid categoery name] param)
  (println "update complete"))

;; The pdfCategoeries table operations
(defn create-categoery-record [pdf-categoery-record]
  (with-db sql/insert-record :pdfCategoeries pdf-categoery-record))

(defn delete-categoery [userid categoery]
  (with-db sql/delete-rows :pdfCategoeries
    ["userid = ? and categoery = ?" userid categoery]))

(defn get-user-categoeries [userid]
  (with-db sql/with-query-results res
    ["select * from pdfCategoeries where userid = ?" userid] (doall res)))

(defn valid-user-categoeries [userid categoery]
  (with-db sql/with-query-results res
    ["select * from pdfCategoeries where userid= ? and categoery = ?" userid categoery]
    (first res)))

(defn get-pdfCategoeries-count [userid categoery]
  (with-db sql/with-query-results
    res ["select * from pdfCategoeries where userid = ? and categoery = ?" userid categoery]
    (first res)))

(defn update-pdfCategoeries-count [userid categoery param]
  (with-db sql/update-values :pdfCategoeries
    ["userid = ? and categoery = ?" userid categoery] param))

;; The comments table operation
(defn create-comment-record [comment-record]
  (with-db sql/insert-record :comments comment-record))

(defn get-pdf-comments [userid categoery name]
  (with-db sql/with-query-results res
    ["select * from comments where pdfowner = ? and pdfcategoery = ? and pdfname = ?" 
     userid categoery name] (doall res)))

;; The favorite table operation
(defn create-favorite-record [favorite-record]
  (with-db sql/insert-record :favorites favorite-record))

(defn delete-favorite-record [userid pdf-owner pdf-categoery pdf-name]
  (with-db sql/delete-rows :favorites
    ["userid = ? and pdfowner = ? and pdfcategoery = ? and pdfname = ?"
     userid pdf-owner pdf-categoery pdf-name]))

(defn get-favorites-by-userid [userid]
  (with-db sql/with-query-results res
    ["select * from favorites where userid = ?" userid] (doall res)))