(ns ctim.rdf.parser.common-test
  (:require [ctim.rdf.parser.common :as sut]
            [clojure.test :refer [deftest is]]))


(deftest ->rdf-test
  (let [uri :ctir/a1]
    (is (= [{:s uri
             :p :ctio/a
             :v 1
             :g sut/default-graph}
            {:s uri
             :p :ctio/b
             :v 2
             :g sut/default-graph}
            {:s uri
             :p :ctio/c
             :v 3
             :g sut/default-graph}]
           (sut/->rdf {:a 1 :b 2 :c 3}
                      uri)))

    (is (= [{:s uri
             :p :ctio/a
             :v 1
             :g sut/default-graph}
            {:s uri
             :p :ctio/a
             :v 2
             :g sut/default-graph}
            {:s uri
             :p :ctio/a
             :v 3
             :g sut/default-graph}]
           (sut/->rdf {:a [1 2 3]} uri)))

    (let [[{s1 :s p1 :p v1 :v g1 :g}
           {s2 :s p2 :p v2 :v g2 :g}
           {s3 :s p3 :p v3 :v g3 :g}] (sut/->rdf {:a {:b 1 :c 2}}
                                                 uri)]
      (is (= g1 g2 g3 sut/default-graph))
      (is (= s1 uri))
      (is (= (namespace v1) "ctir"))
      (is (= v1 s2 s3))
      (is (= p1 :ctio/a))
      (is (= p2 :ctio/b))
      (is (= p3 :ctio/c))
      (is (= 1 v2))
      (is (= 2 v3)))

    (let [[{s1 :s p1 :p v1 :v g1 :g}
           {s2 :s p2 :p v2 :v g2 :g}
           {s3 :s p3 :p v3 :v g3 :g}
           {s4 :s p4 :p v4 :v g4 :g}
           {s5 :s p5 :p v5 :v g5 :g}] (sut/->rdf {:a [{:b 1 :c 2} {:d 3}]}
                                                 uri)]
      (is (= g1 g2 g3 g4 g5 sut/default-graph))
      (is (= s1 s2 uri))
      (is (= (namespace v1) "ctir"))
      (is (= (namespace v2) "ctir"))
      (is (not= v1 v2))
      (is (= v1 s3))
      (is (= v1 s4))
      (is (= v2 s5))
      (is (= p1 :ctio/a))
      (is (= p2 :ctio/a))
      (is (= p3 :ctio/b))
      (is (= p4 :ctio/c))
      (is (= p5 :ctio/d))
      (is (= 1 v3))
      (is (= 2 v4))
      (is (= 3 v5)))))
