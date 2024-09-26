(ns rtable.cheetah
 (:require
  [reagent.core :as reagent]
  [reagent.dom]
  ["cheetah-grid" :as cheetah-grid :refer [ListGrid data]]
   [tech.v3.dataset :as tmlds]
  ))

(def CachedDataSource (.-CachedDataSource data))

(defn create-datasource [ds]
  (println "creating datasource..")
  (let [count (tmlds/row-count ds)
        ds (CachedDataSource. (clj->js {:get (fn [row-idx]
                                               (println "getting row-idx: " row-idx)
                                               (clj->js (tmlds/row-at ds row-idx)))
                                        :length count}))
        ]
    (println "ds count: " count " ds: " ds)
    ds))

(defn render-cheetah [node columns data ds]
  (if ds
    (ListGrid.
      (clj->js {:parentElement node
                :header columns
                :dataSource (create-datasource ds)}))
    (ListGrid.
      (clj->js {:parentElement node
                :header columns
                :records data}))))




(defn cheetah
  [{:keys [style class columns data ds]
    :or {style {}
         class ""}}]
  (let [uuid 28]
    ; https://github.com/reagent-project/reagent/blob/master/doc/CreatingReagentComponents.md
    (reagent/create-class
     {:display-name "cheetah-reagent"
      :reagent-render (fn [{:keys [style class]
                            :or {style {}
                                 class ""}}] ;; remember to repeat parameters
                        [:div {:style style
                               :class class}])
      :component-did-mount (fn [this] ; oldprops oldstate snapshot
                             ;(println "c-d-m: " this)
                             ;(info (str "jsrender init data: " data))
                             (render-cheetah (reagent.dom/dom-node this) columns data ds))
      :component-did-update (fn [this old-argv]
                              (let [new-argv (rest (reagent/argv this))
                                    [arg1] new-argv
                                    {:keys [data-js]} arg1]
                                ;(println "component did update: " this "argv: " new-argv)
                                (render-cheetah (reagent.dom/dom-node this) columns data ds)))})))



