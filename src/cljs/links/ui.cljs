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

(defn value [e]
  (-> e .-target .-value))

(defn input-attr [params get-fn state-atom]
  (merge {:default-value (get-fn @state-atom)
          :on-change (fn [e]
                       (let [val (value e)]
                         (print "Val " val )
                         (swap! state-atom
                                assoc
                                get-fn
                                val)))}
         params))

(defn tags
  "given a list of tags it will render a list of tags"
  [tags]
  [:div.some-padding (map (fn [tag]
                            [:span.tag.is-info.is-medium.tag-padding
                             tag
                             [:button.delete ]]) tags)])
