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
    res ["select * from users limit 10"] (doall res)))


;; The pdfs table operations
(defn create-pdf-record [pdf-record]
  (with-db sql/insert-record :pdfs pdf-record))

(defn delete-pdf-record [username categoery name]
  (with-db sql/delete-rows :pdfs 
    ["userid = ? and categoery = ? and name = ?" username categoery name]))

(defn get-pdf-by-inden [username categoery name]
  (with-db sql/with-query-results
    res ["select * from pdfs where userid = ? and categoery = ? and name = ?"
         username categoery name] (first res)))

(defn get-pdfs [order-by & limit]
  (with-db sql/with-query-results
    res [(str "select * from pdfs order by " order-by " desc" (if limit 
                                                                (str " limit " (first limit))))] 
    (doall res)))

;; use userid categoery and name can find an only pdf record
(defn update-pdf-attr [userid categoery name param]
  (with-db sql/update-values
    :pdfs ["userid = ? and categoery = ? and name = ?"
           userid categoery name] param)
  (println "update complete"))