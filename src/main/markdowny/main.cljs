(ns markdowny.main
  (:require
   [reagent.core :as r]
   [reagent.dom :as rd]
   ["showdown" :as showdown]))

(defonce markdown (r/atom ""))

(defonce showdown-converter (showdown/Converter.))

(defn md->html [md]
  (.makeHtml showdown-converter md))

(defn app []
  [:div
   [:h1 "Hello, from Reagent!"]
   [:textarea
    {:on-change #(reset! markdown (-> % .-target .-value))
     :value @markdown}]

   [:div {:dangerouslySetInnerHTML {:__html (md->html @markdown)}}]
   [:div (md->html @markdown)]])




(defn mount! []
  (rd/render [app]
            (.getElementById js/document "app")))

(defn main! []
  (println "Hi! I am Main")
  (mount!))

(defn reload! []
  (println "Reloaded, yay!")
  (mount!))
