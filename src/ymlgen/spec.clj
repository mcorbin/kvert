(ns ymlgen.spec
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]))

(defn file?
  "Verifies if the provided path is a file"
  [path]
  (let [file (io/file path)]
    (and (.exists file)
         (.isFile file))))

(s/def ::ne-string (s/and string? (complement string/blank?)))
(s/def ::variables (s/map-of keyword? any?))
(s/def ::profile keyword?)
(s/def ::path ::ne-string)
(s/def ::url ::ne-string)

(s/def ::cacert file?)
(s/def ::key file?)
(s/def ::cert file?)
(s/def ::name ::ne-string)

(s/def ::repository (s/keys :req-un [::name ::url]
                            :opt-un [::cacert
                                     ::key
                                     ::cert]))

(s/def ::repositories (s/and (s/coll-of ::repository)
                             (fn [repositories]
                               (= (count repositories)
                                  (->> repositories
                                       (map :name)
                                       set
                                       count)))))

(s/def ::config (s/keys :opt-un [::variables ::profile ::repositories]))

(s/def ::include (s/keys :req-un [::path]
                         :opt-un [::variables ::profile]))
