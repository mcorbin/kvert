(ns kvert.commands.yaml-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [kvert.commands.yaml :as yaml]))

(deftest edn->yaml-test
  (is (= "---\n{}\n" (yaml/edn->yaml {})))
  (is (= "---\nfoo: bar\n" (yaml/edn->yaml {:foo "bar"})))
  (is (= "---\nfoo: bar\n" (yaml/edn->yaml [{:foo "bar"}])))
  (is (= "---\nfoo: bar\n---\na: b\n" (yaml/edn->yaml [{:foo "bar"} {:a "b"}]))))

(deftest build-config-test
  (is (= {:foo "bar" :a 1}
         (yaml/build-config (.getPath (io/resource "edn/config1.edn"))))))

"---\nfoo: bar\nlabels:\n  l1: l1\n  l2: true\nbar: test\njoin: prefix-1\n"

(deftest gen-yaml-test
  (is (= (str "---\nfoo: bar\n"
              "labels:\n"
              "  l1: l1\n"
              "  l2: true\n"
              "bar: test\n"
              "join: prefix-1\n")
         (yaml/gen-yaml {:template-path (.getPath (io/resource "edn/template1.edn"))
                         :config-path (.getPath (io/resource "edn/config2.edn"))})))
  (is (= (str "---\nfoo:\n"
              "  foo: test\n"
              "  bar: t2\n")
         (yaml/gen-yaml {:template-path (.getPath (io/resource "edn/template2.edn"))
                         :config-path (.getPath (io/resource "edn/config2.edn"))}))))
