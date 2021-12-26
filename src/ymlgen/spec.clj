(ns ymlgen.spec
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]))

(defn file?
  "Verifies if the provided path is a file"
  [path]
  (let [file (io/file path)]
    (and (.exists file)
         (.isFile file))))

(s/def ::variables (s/map-of keyword? any?))
(s/def ::profile keyword?)
(s/def ::path string?)

(s/def ::config (s/keys :opt-un [::variables ::profile]))

(s/def ::include (s/keys :req-un [::path]
                         :opt-un [::variables ::profile]))
