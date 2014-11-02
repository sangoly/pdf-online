(ns pdf-online.routes.categoery-manage
  (:require [compojure.core :refer [defroutes GET POST]]
            [pdf-online.views.layout :as layout]
            [pdf-online.util :as util]
            [noir.util.route :refer [restricted]]))

(defn handle-categoery-manage []
  (layout/my-render "categoery_manage.html" {}))

(defroutes categoery-manage-routes
  (GET "/categoery/manage" []
       (restricted (handle-categoery-manage))))