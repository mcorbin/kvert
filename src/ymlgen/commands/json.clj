(ns ymlgen.commands.json
  (:require [clojure.data.json :as json]
            [ymlgen.commands.yaml :as yaml]
            [ymlgen.edn :as edn]))

(defn gen-json
  "Creates yaml from a template and a configuration path"
  [{:keys [template-path config-path]}]
  (let [config (if config-path
                 (yaml/build-config config-path)
                 {})]
    (-> (edn/read-edn-file template-path
                           config)
        json/write-str)))

(defn json-command
  [{:keys [output-path] :as config}]
  (let [result (gen-json config)]
    (if output-path
      (spit output-path result)
      (do (print result)
          (flush)))))
