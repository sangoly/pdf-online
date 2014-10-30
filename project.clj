(defproject pdf-online "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.5"]
                 [ring-server "0.3.1"]
                 [ring/ring-anti-forgery "1.0.0"]
                 [selmer "0.7.2"]
                 [lib-noir "0.9.4"]
                 [environ "1.0.0"]
                 [postgresql/postgresql "9.3-1102.jdbc4"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [ring/ring-codec "1.0.0"]]
  :plugins [[lein-ring "0.8.12"]]
  :ring {:handler pdf-online.handler/app
         :init pdf-online.handler/init
         :destroy pdf-online.handler/destroy}
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.3.1"]]}})
