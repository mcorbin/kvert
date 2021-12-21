(ns ymlgen.commands.yaml
  (:require [ymlgen.edn :as edn]
            ymlgen.readers
            [clj-yaml.core :as yaml]
            [clojure.string :as string]))

(defn edn->yaml
  "Converts an edn datastructure to yaml.
  Handles both values or list of values. If a list is provided the resulting
  yaml will contain several resources separated by `---`."
  [edn]
  (->> (if (sequential? edn)
        edn
        [edn])
       (map #(yaml/generate-string % :dumper-options {:flow-style :block}))
       (string/join "---\n")
       (str "---\n")))

(defn get-env-profile
  "Get the profile from the environment if it's defined."
  []
  (some-> (System/getenv "PROFILE")
          keyword))

(defn build-config
  "Build the configuration used to read edn.
  The configuration will be read from the `config-path`. The profile will also
  be read from the environment variable if needed."
  [config-path]
  (let [profile (get-env-profile)]
    (merge
     (cond-> {}
       profile (assoc :profile profile))
     (edn/read-edn-file config-path {}))))

(defn gen-yaml
  "Creates yaml from a template and a configuration path"
  [{:keys [template-path config-path]}]
  (let [config (if config-path
                 (build-config config-path)
                 {})]
    (-> (edn/read-edn-file template-path
                       config)
        edn->yaml)))

(defn yaml-command
  [{:keys [output-path] :as config}]
  (let [result (gen-yaml config)]
    (if output-path
      (spit output-path result)
      (println result))))
