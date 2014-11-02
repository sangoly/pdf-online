(ns pdf-online.routes.showpdf
  (:require [compojure.core :refer [defroutes GET]]
            [pdf-online.util :as util]
            [pdf-online.models.db :as db]
            [ring.util.response :as resp]))

(defn hand-user-cate-name-request [userid categoery name]
  (try
    (db/update-pdf-attr userid categoery name 
                        {:clicktimes
                         (+ (:clicktimes (db/get-pdf-by-inden userid categoery name)) 1)})
    (catch Exception e
      (println "update clicktime error")))
  (resp/file-response (util/join-path-parts userid util/pdf categoery name)))

(defroutes showpdf-routes
  (GET "/:userid/:categoery/:name" [userid categoery name]
       (hand-user-cate-name-request userid categoery name)))