(ns kvert.commands.edn-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [kvert.commands.edn :as edn]))

(deftest yaml->edn-test
  (is (= [] (edn/yaml->edn "")))
  (is (= [{:foo "bar"}] (edn/yaml->edn "foo: bar")))
  (is (= [{:foo "bar"}] (edn/yaml->edn "---\nfoo: bar")))
  (is (= [{:foo "bar" :test 1} {:a true}] (edn/yaml->edn "---\nfoo: bar\ntest: 1\n---\na: true"))))

(deftest gen-edn-test
  (is (= [{:foo "bar"
           :list [1 2 3]}]
         (edn/gen-edn {:template-path (.getPath (io/resource "yaml/f1.yaml"))})))
  (is (= [{:foo "bar"}
          {:test {:bar "baz" :bool true :int 3}}]
         (edn/gen-edn {:template-path (.getPath (io/resource "yaml/f2.yaml"))}))))
