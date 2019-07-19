(ns ctim.rdf.parser.common)

  (def default-graph "https://cisco.com/ctia")

(def context
  {:dc "http://purl.org/dc/elements/1.1/"
   :ctio "https://cisco.com/ctia/ontology/"
   :ctir "https://cisco.com/ctia/resource/"})

;; types
(def Sighting :ctio/Sighting)
(def Observable :ctio/Obervable)
(def ObservableMd5 :ctio/ObervableMd5)
(def ObservableSha256 :ctio/ObervableSha256)
(def ObservableSha1 :ctio/ObervableSha1)
(def ObservableDomain :ctio/ObervableDomain)
(def ObservableIp :ctio/ObervableIp)
(def ObservableUrl :ctio/ObervableUrl)

(def obs-type->ctio {"sha1" ObservableSha1
                     "sha256" ObservableSha256
                     "md5" ObservableMd5
                     "ip" ObservableIp
                     "url" ObservableUrl
                     "domain" ObservableDomain})

(defn ctim-resource
  [suffix]
  (keyword "ctir" (str suffix)))

(defn ctim-property
  [prop-name]
  (keyword "ctio" (name prop-name)))

(defn uuid
  []
  (ctim-resource (java.util.UUID/randomUUID)))

(defn blank-node
  []
  (str "_:" (java.util.UUID/randomUUID)))

(defn reify-observable
  [{obs-type :type value :value} graph]
  (let [uri (ctim-resource value)]
    [{:s uri
      :p :rdf/type
      :v (obs-type->ctio value)
      :g graph}
     {:s uri
      :p :ctio/value
      :v value
      :g graph}]))

(defn ->rdf
  ([doc] (->rdf doc (uuid) default-graph))
  ([doc id] (->rdf doc id default-graph))
  ([doc id graph]
   (cond
     (map? doc) (->> (clojure.walk/keywordize-keys doc)
                     (mapcat (fn [[k v]]
                               (let [prop-uri (ctim-property k)]
                                 (cond
                                   (map? v) (let [values (->rdf v)]
                                              (cons {:s id :p prop-uri  :v (-> values first :s) :g graph}
                                                    values))
                                   (and (coll? v) (map? (first v))) (let [values (map ->rdf v)]
                                                                      (concat (map #(assoc {:s id :p prop-uri :g graph}
                                                                                           :v (-> % first :s))
                                                                                   values)
                                                                              (apply concat values)))
                                   (coll? v) (map #(array-map :s id
                                                              :p prop-uri
                                                              :g graph
                                                              :v %)
                                                  v)
                                   :else [{:s id :p prop-uri  :v v :g graph}])))))
     (coll? doc) (map #(->rdf %) doc)
     :else doc)))
