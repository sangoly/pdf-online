(ns pdf-online.routes.showpdf
  (:require [compojure.core :refer [defroutes GET]]
            [pdf-online.util :as util]
            [ring.util.response :as resp]))

;  (->
;    (resp/file-response (util/join-path-parts userid categoery name))
;    (resp/header "Content-Disposition" (str "filename=" name))
;    (resp/content-type "application/pdf"))
(defn hand-user-cate-name-request [userid categoery name]
  (resp/file-response (util/join-path-parts userid "iamge" "th_ieldrunners.png")))

(defroutes showpdf-routes
  (GET "/:userid/:categoery/:name" [userid categoery name]
       (hand-user-cate-name-request userid categoery name)))