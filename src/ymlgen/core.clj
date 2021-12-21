(ns ymlgen.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.tools.cli :as cli]
            [ymlgen.commands.edn :as edn-cmd]
            [ymlgen.commands.yaml :as yaml-cmd])
  (:gen-class))

(defn file?
  "Verifies if the provided path is a file"
  [path]
  (let [file (io/file path)]
    (and (.exists file)
         (.isFile file))))

(def cli-options
  [["-t" "--template TEMPLATE-PATH" "Path to the template file"
    :validate [file? "Should be a path to a template file"]]
   ["-c" "--config CONFIGURATION-PATH" "Path to the configuration file"
    :validate [file? "Should be a path to a configuration file"]]
   ["-o" "--output OUTPUT-PATH" "Path to the output file"]
   ["-h" "--help"]])

(defn error!
  [msg exit-code]
  (binding [*out* *err*]
    (println msg)
    (System/exit exit-code)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [opts (cli/parse-opts args cli-options)
        command (first args)
        template-path (-> opts :options :template)
        config-path (-> opts :options :config)
        output-path (-> opts :options :output)]
    (when-let [errors (:errors opts)]
      (error! (str "errors: "
                   (string/join ", " errors))
              1))
    (when-not template-path
      (error! "The option -t (--template) is mandatory" 1))
    (try
      (case command
        "yaml" (yaml-cmd/yaml-command {:template-path template-path
                                       :config-path config-path
                                       :output-path output-path})
        "edn" (edn-cmd/edn-command {:template-path template-path
                                    :output-path output-path})
        (error! (str "Invalid command " command) 1))
      (catch Exception e
        (error! (.getMessage e) 2)))))

