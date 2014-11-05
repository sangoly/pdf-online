(ns pdf-online.views.layout
  (:require [hiccup.page :refer [html5 include-css]]
            [pdf-online.util :refer [get-template-path]]
            [pdf-online.util]
            [ring.util.response :refer [content-type response]]
            [compojure.response :refer [Renderable]]
            [noir.session :as session]
            [selmer.parser :as parser]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn utf-8-response [html]
  (content-type (response html) "text/html; charset=UTF-8"))

(deftype RenderablePage [template params]
  Renderable
  (render [this request]
    (->> (assoc params
                :context (:context request)
                :user    (session/get :user)
                :antiforgery (anti-forgery-field)
                :errors (session/get! :errors))
         (parser/render-file (get-template-path template))
         utf-8-response)))

(defn my-render [template & [params]]
  (RenderablePage. template params))