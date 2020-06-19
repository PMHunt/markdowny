(ns markdowny.main
  (:require
   [reagent.core :as r]
   [reagent.dom :as rd]
   ["showdown" :as showdown]))

; (defonce markdown (r/atom ""))
                                        ; (defonce html (r/atom ""))

(defonce flash-message (r/atom nil))
(defonce flash-timeout (r/atom nil)) ; used to kill previous flash on new event

(defn flash
  ([text]
   (flash text 3000))
  ([text ms]
   (js/clearTimeout @flash-timeout)
   (reset! flash-message text)
   (js/setTimeout #(reset! flash-message nil) ms)
   (reset! flash-timeout
           (js/setTimeout #(reset! flash-message nil) ms))))

(defonce showdown-converter (showdown/Converter.))

(defn md->html [md]
  (.makeHtml showdown-converter md))

(defn html->md [html]
  (.makeMarkdown showdown-converter html))

;;; bettter way than html + md  atoms
(defonce text-state (r/atom {:format :md
                             :value ""}))

(defn ->md [{:keys [format value]}]
  (case format
    :md value
    :html (html->md value)))


(defn ->html [{:keys [format value]}]
  (case format
    :html value
    :md (md->html value)))

;; copy-to-clipboard is port of:
;; https://hackernoon.com/copying-text-to-clipboard-with-javascript-df4d4988697f

(defn copy-to-clipboard [s]
  (let [el (.createElement js/document "textarea") ; create temp. working area in DOM
        selected (when (pos? (-> js/document .getSelection .-rangeCount)) ; existing selection?
                   (-> js/document .getSelection (.getRangeAt 0)))] ; store selection
    (set! (.-value el) s) ; put string param into our new <textarea> element
 ;   (println "after el is set" (.-value el))
    (.setAttribute el "readonly" "") ; make `el`readonly, so can't mess with it
    (set! (-> el .-style .-position) "absolute")
    (set! (-> el .-style .-left) "-9999px") ; banish it to outer darkness
    (-> js/document .-body (.appendChild el))
    (.select el) ; select whatever is in the text area
    (.execCommand js/document "copy") ; copy selection, only works in user event
  ;  (println "after copy to clipboard")
    (-> js/document .-body (.removeChild el)) ; get rid of temporary element `el`
    (when selected ; restore any original selection
      (-> js/document .getSelection .removeAllRanges)
      (-> js/document .getSelection (.addRange selected)))))

(defn app []
  [:div.app
   [:div.flash-message
    {:style {:transform (if @flash-message
                          "scaleY(1)"
                          "scaleY(0)")
             :transition "transform 0.2s ease-out"}}
    @flash-message]
   [:h1 "A simple Markdown editor"]
   [:div.bothwindows
    [:div.mdwindow
     [:h2.heading "Markdown"]
     [:textarea.mdtext
      {:on-change (fn [e]  ; reset! ok in JS, and swap! doesn't work
                    (reset! text-state {:format :md
                                        :value (-> e .-target .-value)})  )
       :value (->md @text-state)}]
     [:button.copybtn
      {:on-click (fn []
                   (copy-to-clipboard (->md @text-state))
                   (flash "Markdown copied to clipboard"))}
      "Copy Markdown"]]

    [:div.htmlwindow
     [:h2.heading "HTML"]
     [:textarea.mdtext
      {:on-change (fn [e]
                    (reset! text-state {:format :html
                                        :value (-> e .-target .-value)}))
       :value (->html @text-state)}]
     [:button.copybtn
      {:on-click (fn []
                   (copy-to-clipboard (->html @text-state))
                   (flash "HTML copied to clipboard"))}
      "Copy HTML"]]

    [:div.previewwindow
     [:h2.heading "HTML Preview"]
     [:div {:dangerouslySetInnerHTML {:__html (->html @text-state)}}]]]])

(defn mount! []
  (rd/render [app]
            (.getElementById js/document "app")))

(defn main! []
  (println "Hi! I am Main")
  (mount!))

(defn reload! []
  (println "Reloaded, yay!")
  (mount!))
