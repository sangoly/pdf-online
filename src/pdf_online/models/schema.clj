(ns pdf-online.models.schema
  (:require [pdf-online.models.db :as db]
            [clojure.java.jdbc :as sql]))

;; Create the user table
(defn create-user-table []
  (db/with-db sql/create-table
    :users
    [:id "varchar(32) PRIMARY KEY not null"]
    [:pass "varchar(100) not null"]
    [:timestamp "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"]
    [:superuser "boolean default false not null"]
    [:statewords "varchar(200) not null"]
    [:image "varchar(100)"]))

(defn create-pdfs-table []
  (db/with-db sql/create-table
    :pdfs
    [:userid "varchar(32) REFERENCES users(id) ON DELETE CASCADE not null"]
    [:categoery "varchar(50) not null"]
    [:name "varchar(100) not null"]
    [:clicktimes "Integer not null default 0"]
    [:introduce "varchar(300)"]
    [:goodtimes "Integer not null default 0"]
    [:badtimes "Integer not null default 0"]
    [:timestamp "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"]))

;;DANGEROUS OPERATION
(defn drop-table [table]
  (db/with-db sql/drop-table table))