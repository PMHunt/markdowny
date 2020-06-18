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
  (let [el (.createElement js/document "textarea") ; create <textarea> element
        selected (when (pos? (-> js/document .getSelection .-rangeCount)) ; existing selection?
                   (-> js/document .getSelection (.getRangeAt 0)))] ; store selection
    (set! (.-value el) s) ; put string param into our new <textarea> element
 ;   (println "after el is set" (.-value el))
    (.setAttribute el "readonly" "") ; readonly so people can't mess with it
    (set! (-> el .-style .-position) "absolute")
    (set! (-> el .-style .-left) "-9999px") ; banish it to outer darkness
    (-> js/document .-body (.appendChild el))
    (.select el) ; select whatever is in the text area
    (.execCommand js/document "copy") ; copy selection, only works in user event
  ;  (println "after copy to clipboard")
    (-> js/document .-body (.removeChild el))
    (when selected ; restore any original selection
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
     [:button.copybtn
      {:on-click #(copy-to-clipboard @markdown)}
      "Copy Markdown"]]
    [:div.htmlwindow
     [:h2 "HTML preview"]
     [:div {:dangerouslySetInnerHTML {:__html (md->html @markdown)}}]
     [:button.copybtn
      {:on-click #(copy-to-clipboard (md->html @markdown))}
      "Copy HTML"]]]])

(defn mount! []
  (rd/render [app]
            (.getElementById js/document "app")))

(defn main! []
  (println "Hi! I am Main")
  (mount!))

(defn reload! []
  (println "Reloaded, yay!")
  (mount!))
