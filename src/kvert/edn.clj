(ns kvert.edn
  (:require [aero.core :as aero]))

(defn read-edn-file
  "Read an edn file using aero"
  [path opts]
  (try
    (aero/read-config path opts)
    (catch Exception e
      (throw (ex-info (format "Failed to read the EDN file %s: %s"
                              path
                              (.getMessage e))
                      {})))))

