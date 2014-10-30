(ns pdf-online.models.db
  (:require [clojure.java.jdbc :as sql]))

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


;; The pdfs table operations
(defn create-pdf-record [pdf-record]
  (with-db sql/insert-record :pdfs pdf-record))

(defn delete-pdf-record [username categoery name]
  (with-db sql/delete-rows :pdfs 
    ["userid = ? and categoery = ? and name = ?" username categoery name]))

(defn get-pdfs [params]
  (with-db sql/with-query-results res
    [(str "select * from pdfs where ")] (doall res)))