(ns links.aviana
  (:require [re-frame.core :as rf]
            [snow.effects :as eff]
            ))

(def aviana-url "https://aviana.herokuapp.com")

(rf/reg-event-fx
 :signup
 (fn [cofx [_ user]]
   (eff/request-eff {:method :post
                     :uri (str aviana-url "/user/signup")
                     :on-success [:signup-complete]}
                    cofx)))

(rf/reg-event-fx
 :signup-complete
 (fn [cofx [_ data]]
   (println "data is " data)
   (eff/naviage-eff {:route :login}
                    cofx)))
