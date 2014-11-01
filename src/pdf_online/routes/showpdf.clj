(ns pdf-online.routes.showpdf
  (:require [compojure.core :refer [defroutes GET]]
            [pdf-online.util :as util]
            [ring.util.response :as resp]))

(defn hand-user-cate-name-request [userid categoery name]
  (resp/file-response (util/join-path-parts userid util/pdf categoery name)))

(defroutes showpdf-routes
  (GET "/:userid/:categoery/:name" [userid categoery name]
       (hand-user-cate-name-request userid categoery name)))