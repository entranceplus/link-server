(ns links.app
  (:require [snow.comm.core :as comm]
            [snow.router :as router]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [stylefy.core :as stylefy :refer [use-style]]
            ["react-pose" :as rpose :default posed]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Available routes in our app ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def route-map {:home "/"
                :select-topics "/topics"
                :sources "/sources"})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; css used for login page ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def loading-after {:width "40%"
                    :height "100vh"
                    :transition "all 0.3s ease-in"
                    :background "#374A67" })

(def home-container {:display "flex"
                     :flex-direction "column"
                     :justify-content "center"
                     :align-items "center"
                     :height "100vh"
                     })

(def form-container {:display "flex"
                     :flex-direction "column"
                     :justify-content "center"
                     :align-items "stretch"
                     :margin-top "70px"})

(def input-style {:text-align "center"
                  :background-color "#CB9CF2"
                  :height "30px"
                  :line-height "30px"
                  :border "1px solid purple"
                  :border-radius "2px"})

(def input-container-style {:padding "5px 0px 5px 0px"})

;;;;;;;;;;;;;;;;;;;;;;;;;;
;; login-page component ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn home-page []
  [:div (use-style home-container)
   [:h1 (use-style {:flex-grow "0"})  "Around"]
   [:div "Somethings are around"]
   [:div (use-style form-container)
    [:div (use-style input-container-style)
     [:input (use-style  input-style
                         {:type "text"
                          :placeholder "Enter username"})]]
    [:div (use-style input-container-style)
     [:input (use-style input-style
                        {:type "password"
                         :placeholder "Enter password"})]]
    [:div (use-style input-container-style)
     [:div (use-style input-style) "Get Started"]]]])


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; select topic component could go here ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn select-topics []
  [:div "Hi User, You will select topics here."])

;;;;;;;;;;;;;;;;;;;;;;;;;
;; top level component ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(defn page
  [route params]
  "based on the route load the correct child"
  [:div (stylefy/use-style loading-after)
   (case route
     :home [home-page]
     :select-topics [select-topics]
     :sources [:div "Sources"]
     [:div "Not Found"])])

;; event handler to ensure navigation works
(rf/reg-event-fx
 :navigate
 (fn [{:keys [db]} [_ page param]]
   (let [db (assoc db :page page :page-param param)]
     (case page
       {:db db}))))

(defn on-navigate
  "Function called on route change. This loads the page component passing it the
   route the user currently is at."
  [route params query]
  (rf/dispatch-sync [:navigate {:route route
                                :params params
                                :perform? false}])
  (r/render [page route params]
            (js/document.getElementById "root")))


(defn main
  []
  "start stuff"
  ;; start communication with backend
  (comm/start!)

  ;; initialize stylefy (used for writing css in cljs)
  (stylefy/init)

  ;; start front end router
  (router/start! route-map on-navigate)

  ;; do intial animation
  (js/anime (clj->js  {:targets ".app"
                       :translateX [{:value 0 :duration 1200}
                                    {:value "-60%"
                                     :duration 800}]
                       :duration 2000}))

  ;; print as always
  (println "Well.. Hello there!!!!"))


(main)
