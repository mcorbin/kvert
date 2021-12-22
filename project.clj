(defproject ymlgen "0.2.0"
  :description "generate yaml files from edn"
  :url "https://github.com/mcorbin/ymlgen"
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[aero "1.1.6"]
                 [clj-commons/clj-yaml "0.7.0"]
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/data.json "2.4.0"]
                 [org.clojure/tools.cli "1.0.206"]]
  :main ^:skip-aot ymlgen.core
  :target-path "target/%s"
  :profiles {:dev {:resource-paths ["resources" "test/resources" "gen-resources"]
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]
                   :dependencies [[pjstadig/humane-test-output "0.11.0"]]}
             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
