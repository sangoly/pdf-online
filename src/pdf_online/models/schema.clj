(ns pdf-online.models.schema
  (:require [pdf-online.models.db :as db]
            [clojure.java.jdbc :as sql]))

;; Create the user table
(defn create-user-table []
  (db/with-db sql/create-table
    :users
    [:id "varchar(32) PRIMARY KEY not null"]
    [:pass "varchar(100) not null"]
    [:timestamp "TIMESTAMP(0) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP"]
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
    [:timestamp "TIMESTAMP(0) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP"]
    ["constraint pk_pdfs primary key(userid, categoery, name)"]))

(defn create-pdf-categoeries-table []
  (db/with-db sql/create-table
    :pdfCategoeries
    [:userid "varchar(32) REFERENCES users(id) ON DELETE CASCADE not null"]
    [:categoery "varchar(50) not null"]
    [:count "Integer not null default 0"]
    [:timestamp "TIMESTAMP(0) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP"]))

(defn create-comments-table []
  (db/with-db sql/create-table
    :comments
    [:pdfowner "varchar(32) not null"]
    [:pdfcategoery "varchar(50) not null"]
    [:pdfname "varchar(100) not null"]
    [:fromuser "varchar(32) not null"]
    [:touser "varchar(32)"]
    [:timestamp "TIMESTAMP(0) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP"]
    [:content "text not null"]
    ["constraint comments foreign key(pdfowner, pdfcategoery, pdfname) references pdfs(userid, categoery, name) ON DELETE CASCADE"]))

(defn create-favorite-table []
  (db/with-db sql/create-table
    :favorites
    [:userid "varchar(32) REFERENCES users(id) ON DELETE CASCADE not null"]
    [:pdfowner "varchar(32) not null"]
    [:pdfcategoery "varchar(50) not null"]
    [:pdfname "varchar(100) not null"]
    ["constraint favorites foreign key(pdfowner, pdfcategoery, pdfname) references pdfs(userid, categoery, name) ON DELETE CASCADE"]))
;;DANGEROUS OPERATION
(defn drop-table [table]
  (db/with-db sql/drop-table table))