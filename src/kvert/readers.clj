(ns kvert.readers
  (:require [aero.core :as aero]
            [clojure.spec.alpha :as s]
            [kvert.spec :as spec]))

(defmethod aero/reader 'kvert/var
  [opts _ value]
  (when-not (keyword? value)
    (throw (ex-info "The argument of #kvert/var should be a keyword."
                    {:variable value})))
  (let [variables (:variables opts)]
    (if-let [result (get variables value)]
      result
      (throw (ex-info (format "Variable %s not found" value) {})))))


(defn repository->client
  [config])

(defn get-from-repository
  [repositories repository path]


  )

(defmethod aero/reader 'kvert/include
  [{:keys [resolver source] :as opts} _ value]
  (let [path (:path value)
        variables (:variables value {})
        repositories (:repositories opts)
        repository (:include/repository value)
        profile (:profile value)
        _ (when-not (s/valid? ::spec/include value)
            (throw (ex-info (format "Invalid #kvert/include configuration for path %s" path)
                            {})))

        result (aero/read-config
                (if (map? resolver)
                  (get resolver path)
                  (resolver source path))
                (cond-> (update opts :variables merge variables)
                  profile (assoc :profile profile)))]
    (when (:aero/missing-include result)
      (throw (ex-info (format "#kvert/include: file %s not found" path)
                            {})))
    result))
