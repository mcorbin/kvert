(ns kvert.spec-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [kvert.spec :as spec]))

(deftest repositories-test
  (is (s/valid? ::spec/repositories []))
  (is (s/valid? ::spec/repositories [{:name "foo" :url "http://mcorbin.fr"}]))
  (is (not (s/valid? ::spec/repositories [{:name "foo" :url "http://mcorbin.fr"}
                                          {:name "foo" :url "http://mcorbin.fr"}
                                          ])))
  (is (s/valid? ::spec/repositories [{:name "foo" :url "http://mcorbin.fr"}
                                     {:name "bar" :url "http://mcorbin.fr"}])))
