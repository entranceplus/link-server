(ns links.ui)

(defn button [{loading? :loading?
               text :text
               on-click :on-click
               def-class :def-class}]
  [:div.field
   [:p.control
    [:button.button
     {:class (if loading? (str "is-loading" " " def-class) def-class)
      :on-click on-click}
     text]]])

(defn input-field [attrs]
  [:div.field>p.control>input.input attrs])

(defn input-attr [params get-fn state-atom]
  (merge {:value (get-fn @state-atom)
          :on-change #(swap! state-atom assoc get-fn (-> % .-target .-value))}
         params))
