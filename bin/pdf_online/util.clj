(ns pdf-online.util
  (:require [clojure.string :refer [join]]
            [clojure.java.io :as clo-io])
  (:import [java.io File]))

;; Template operation
(defn get-template-path [basename]
  (join File/separator ["pdf_online" "template" basename]))

(def pdf "pdffiles")
(def image "image")
(def default-statewords "说点啥好呢？")
(def default-headimage "default_headimage.jpg")
;; The consts
(def user-file-root "userfiles")
(def one-level-folders ["pdffiles" "image" "test" "中文测试"])

(defn join-path-parts [& parts]
  (join File/separator (cons user-file-root parts)))

;; Init the new user's folders
(defn create-new-user-folder [username]
  (let [user-root-path (join-path-parts username)
        user-root-folder (File. user-root-path)]
    (if-not (.exists user-root-folder)
      (do
        (.mkdirs user-root-folder)
        (doall
          (for [tmp-dir (seq one-level-folders)] 
					  (let [tar-file (File. (str user-root-path File/separator tmp-dir))]
					    (if-not (.exists tar-file)
					      (.mkdirs tar-file)))))))))


;; Handler the upload files
(defn ensure-exists [path]
  (println path)
  (let [dir (File. path)]
    (println (.getAbsolutePath dir))
    (if-not (.exists dir)
      (.mkdirs dir))))

(defn save-upload-file
  [{filename :filename tempfile :tempfile} path]
  (ensure-exists path) 
  (clo-io/copy tempfile (clo-io/file path filename)))

(defmacro join-expand-params [body] `(~join-path-parts ~@body))

;; Detele a directory
(defn delete-directory [user-root-path]
  (let [func (fn [func f]
               (when (.isDirectory f)
                 (doseq [sub (.listFiles f)]
                   (func func sub)))
               (clo-io/delete-file f))]
    (func func (clo-io/file user-root-path))))

;; The tempore fucntion for translate keyword to string
(defn key-to-str [key]
  (if-not (keyword? key)
    (str key)
    (subs (str key) 1)))