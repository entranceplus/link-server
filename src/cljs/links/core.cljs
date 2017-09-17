(ns links.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ajax.core :refer [GET POST]]
            [links.ajax :refer [load-interceptors!]]
            [links.ui :as ui]
            [links.events])
  (:import goog.History))

(defn home-page []
  (let [link-data (r/atom {})
        link->seq (fn [link-data]
                    (-> link-data
                        :tags
                        (clojure.string/split ",")))]
    (fn []
      [:div.section>div.container
       [:h1.title "Links"]
       [:div.columns>div.is-half.is-offset-one-quarter
        (ui/input {:type "text"
                   :placeholder "Link"}
                  :link
                  link-data)
        [ui/tags (link->seq @link-data)]
        (ui/input {:type "text"
                   :placeholder "Enter Tags(comma separated)"}
                  :tags
                  link-data)
        ;; [:div (map (fn [fruit]
        ;;              [:li fruit]) (link->seq @link-data))]
        [ui/button {:text "Add Links"
                    :on-click (fn [e]
                                (print @link-data))}]]])))

(def pages
  {:home #'home-page})

(defn page []
  [:div
   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:set-active-page :home]))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(rf/dispatch [:set-docs %])}))

(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
