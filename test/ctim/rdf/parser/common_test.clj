(ns ctim.rdf.parser.common-test
  (:require [ctim.rdf.parser.common :as sut]
            [clojure.test :refer [deftest is]]))

(deftest ->rdf-test
  (is (= '({:s "https://cisco.com/ctia/resource/1"
            :p "https://cisco.com/ctia/ontology/a"
            :v 1}
           {:s "https://cisco.com/ctia/resource/1"
            :p "https://cisco.com/ctia/ontology/b"
            :v 2}
           {:s "https://cisco.com/ctia/resource/1"
            :p "https://cisco.com/ctia/ontology/c"
            :v 3})
         (sut/->rdf {:a 1
                     :b 2
                     :c 3} "https://cisco.com/ctia/resource/1")))

  (is (= '({:s "https://cisco.com/ctia/resource/1"
            :p "https://cisco.com/ctia/ontology/a"
            :v 1}
           {:s "https://cisco.com/ctia/resource/1"
            :p "https://cisco.com/ctia/ontology/a"
            :v 2}
           {:s "https://cisco.com/ctia/resource/1"
            :p "https://cisco.com/ctia/ontology/a"
            :v 3})
         (sut/->rdf {:a [1 2 3]} "https://cisco.com/ctia/resource/1")))

  (let [[{s1 :s p1 :p v1 :v}
         {s2 :s p2 :p v2 :v}
         {s3 :s p3 :p v3 :v}] (sut/->rdf {:a {:b 1 :c 2}}
                                "https://cisco.com/ctia/resource/1")]
    (is (= s1 "https://cisco.com/ctia/resource/1"))
    (is (clojure.string/starts-with? v1 "https://cisco.com/ctia/resource/"))
    (is (= v1 s2 s3))
    (is (= p1 "https://cisco.com/ctia/ontology/a"))
    (is (= p2 "https://cisco.com/ctia/ontology/b"))
    (is (= p3 "https://cisco.com/ctia/ontology/c"))
    (is (= 1 v2))
    (is (= 2 v3)))

  (let [[{s1 :s p1 :p v1 :v}
         {s2 :s p2 :p v2 :v}
         {s3 :s p3 :p v3 :v}
         {s4 :s p4 :p v4 :v}
         {s5 :s p5 :p v5 :v}] (sut/->rdf {:a [{:b 1 :c 2} {:d 3}]}
                                         "https://cisco.com/ctia/resource/1")]
    (is (= s1 s2 "https://cisco.com/ctia/resource/1"))
    (is (clojure.string/starts-with? v1 "https://cisco.com/ctia/resource/"))
    (is (clojure.string/starts-with? v2 "https://cisco.com/ctia/resource/"))
    (is (not= v1 v2))
    (is (= v1 s3))
    (is (= v1 s4))
    (is (= v2 s5))
    (is (= p1 "https://cisco.com/ctia/ontology/a"))
    (is (= p2 "https://cisco.com/ctia/ontology/a"))
    (is (= p3 "https://cisco.com/ctia/ontology/b"))
    (is (= p4 "https://cisco.com/ctia/ontology/c"))
    (is (= p5 "https://cisco.com/ctia/ontology/d"))
    (is (= 1 v3))
    (is (= 2 v4))
    (is (= 3 v5))))
