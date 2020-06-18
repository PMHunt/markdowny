(ns markdowny.main
  (:require
   [reagent.core :as r]
   [reagent.dom :as rd]
   ["showdown" :as showdown]))

(defonce markdown (r/atom ""))

(defonce showdown-converter (showdown/Converter.))

(defn md->html [md]
  (.makeHtml showdown-converter md))

;; copy-to-clipboard is port of:
;; https://hackernoon.com/copying-text-to-clipboard-with-javascript-df4d4988697f

(defn copy-to-clipboard [s]
  (let [el (.createElement js/document "textarea")
        selected (when (pos? (-> js/document .getSelection .-rangeCount))
                   (-> js/document .getSelection (.getRangeAt 0)))]
    (set! (.-value el) s)
    (println "after el is set" (.-value el))
    (.setAttribute el "readonly" "")
    (set! (-> el .-style .-position) "absolute")
    (set! (-> el .-style .-left) "-9999px")
    (-> js/document .-body (.appendChild el))
    (.select el)
    (.execCommand js/document "copy")
    (println "after copy to clipboard")
    (-> js/document .-body (.removeChild el))
    (when selected
      (-> js/document .getSelection .removeAllRanges)
      (-> js/document .getSelection (.addRange selected)))))

(defn app []
  [:div.app
   [:h1 "Markdowny - A simple Markdown editor"]
   [:div.bothwindows
    [:div.mdwindow
     [:h2 "Markdown"]
     [:textarea.mdtext
      {:on-change #(reset! markdown (-> % .-target .-value)) ; swap! no work
       :value @markdown}]
     [:button
      {:on-click #(copy-to-clipboard @markdown)}
      "Copy to clipboard"]]
    [:div.htmlwindow
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
