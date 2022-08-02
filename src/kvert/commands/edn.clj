(ns kvert.commands.edn
  (:require [clj-yaml.core :as yaml]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]))

(extend-protocol yaml/YAMLCodec
  java.util.ArrayList
  (decode [data keywords]
    (mapv #(yaml/decode % keywords) data)))

(defn parse-string-all
  [^String string & {:keys [unsafe mark keywords max-aliases-for-collections allow-recursive-keys allow-duplicate-keys] :or {keywords true}}]
  (for [v (.loadAll (yaml/make-yaml :unsafe unsafe
                                    :mark mark
                                    :max-aliases-for-collections max-aliases-for-collections
                                    :allow-recursive-keys allow-recursive-keys
                                    :allow-duplicate-keys allow-duplicate-keys)
                    string)]
    (yaml/decode v
                 keywords)))

(defn yaml->edn
  "Converts a yaml string to edn"
  [yaml-string]
  (vec (parse-string-all yaml-string :keywords true)))

(defn gen-edn
  "Generates edn from a yaml file"
  [{:keys [template-path]}]
  (-> (try (slurp template-path)
           (catch Exception e
             (throw (ex-info (format "Fail to read yaml file %s: %s" template-path
                                     (.getMessage e))
                             {}))))
      yaml->edn))

(defn edn-command
  [{:keys [output-path] :as config}]
  (let [result (gen-edn config)]
    (if output-path
      (pprint/pprint result (io/writer output-path))
      (pprint/pprint result))))
