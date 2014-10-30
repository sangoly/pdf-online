(ns pdf-online.models.schema
  (:require [pdf-online.models.db :as db]
            [clojure.java.jdbc :as sql]))

;; Create the user table
(defn create-user-table []
  (db/with-db sql/create-table
    :users
    [:id "varchar(32) PRIMARY KEY"]
    [:pass "varchar(100)"]
    [:timestamp "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"]
    [:superuser "boolean"]
    [:statewords "varchar(200)"]
    [:image "varchar(100)"]))

;;DANGEROUS OPERATION
(defn drop-table [table]
  (db/with-db sql/drop-table table))