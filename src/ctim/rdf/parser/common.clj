(ns ctim.rdf.parser.common
  (:require [edn-ld.core :as edn]
            [edn-ld.common :as ednc]))

(def context
  {:dc "http://purl.org/dc/elements/1.1/"
   :ctio "https://cisco.com/ctia/ontology/"
   nil  "https://cisco.com/ctia/ontology/"
   :ctir "https://cisco.com/ctia/resource/"})

(def expand (partial edn/expand context))

;; types
(def Observable (expand :ctio:Obervable))
(def Sighting (expand :ctio:Sighting))

(defn ctim-resource
  [suffix]
  (->> suffix
       (str "ctir:")
       keyword
       expand))

(defn ctim-property
  [prop-name]
  (-> (str "ctio:" (name prop-name))
      keyword
      expand))

(defn uuid
  []
  (ctim-resource (java.util.UUID/randomUUID)))

(defn ->rdf
  ([doc] (->rdf doc (uuid)))
  ([doc id]
   (cond
     (map? doc) (->> (clojure.walk/keywordize-keys doc)
                     (mapcat (fn [[k v]]
                               (let [prop-uri (ctim-property k)]
                                 (cond
                                   (map? v) (let [values (->rdf v)]
                                              (cons {:s id :p prop-uri  :v (-> values first :s)}
                                                    values))
                                   (and (coll? v) (map? (first v))) (let [values (map ->rdf v)]
                                                                      (concat (map #(assoc {:s id :p prop-uri}
                                                                                           :v (-> % first :s))
                                                                                   values)
                                                                              (apply concat values)))
                                   (coll? v) (map #(array-map :s id
                                                              :p prop-uri
                                                              :v %)
                                                  v)
                                   :else [{:s id :p prop-uri  :v v}])))))
     (coll? doc) (map #(->rdf %) doc)
     :else doc)))
