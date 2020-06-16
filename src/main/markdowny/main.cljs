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
   [:h1 "Markdowny - A simple Markdown editor"]
   [:div
    {:style {:display :flex}}
    [:div
     {:style {:flex "1"}}
     [:h2 "Markdown"]
     [:textarea
      {:on-change #(reset! markdown (-> % .-target .-value))
       :value @markdown
       :style {:resize "none"
               :height "400px"
               :width "100%"}}]]
    [:div
     {:style {:flex "1"
              :padding-left "2em"}}
     [:h2 "HTML preview"]
     [:div {:dangerouslySetInnerHTML {:__html (md->html @markdown)}}]]]])

(defn mount! []
  (rd/render [app]
            (.getElementById js/document "app")))

(defn main! []
  (println "Hi! I am Main")
  (mount!))

(defn reload! []
  (println "Reloaded, yay!")
  (mount!))
