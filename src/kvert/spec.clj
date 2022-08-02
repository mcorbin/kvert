(ns kvert.spec
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

(s/def ::repository (s/and (s/keys :req-un [::name ::url]
                                   :opt-un [::cacert
                                            ::key
                                            ::cert])
                           (fn [repo]
                             (or (and (nil? (:key repo))
                                      (nil? (:cert repo))
                                      (nil? (:cacert repo)))
                                 (and (string? (:key repo))
                                      (string? (:cert repo))
                                      (string? (:cacert repo)))))))

(s/def ::repositories (s/and (s/coll-of ::repository)
                             (fn [repositories]
                               (= (count repositories)
                                  (->> repositories
                                       (map :name)
                                       set
                                       count)))))

(s/def :include/repository ::ne-string)

(s/def ::config (s/keys :opt-un [::variables ::profile ::repositories]))

(s/def ::include (s/and (s/keys :req-un [::path]
                                :opt-un [::variables
                                         ::profile
                                         :include/repository])))
