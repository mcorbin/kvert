(ns ymlgen.readers
  (:require [aero.core :as aero]
            [clojure.spec.alpha :as s]
            [ymlgen.spec :as spec]))

(defmethod aero/reader 'ymlgen/var
  [opts _ value]
  (when-not (keyword? value)
    (throw (ex-info "The argument of #ymlgen/var should be a keyword."
                    {:variable value})))
  (let [variables (:variables opts)]
    (if-let [result (get variables value)]
      result
      (throw (ex-info (format "Variable %s not found" value) {})))))

(defmethod aero/reader 'ymlgen/include
  [{:keys [resolver source] :as opts} _ value]
  (let [path (:path value)
        variables (:variables value {})
        profile (:profile value)
        _ (when-not (s/valid? ::spec/include value)
            (throw (ex-info (format "Invalid #ymlgen/include configuration for file %s" path)
                            {})))
        result (aero/read-config
                (if (map? resolver)
                  (get resolver path)
                  (resolver source path))
                (cond-> (update opts :variables merge variables)
                  profile (assoc :profile profile)))]
    (when (:aero/missing-include result)
      (throw (ex-info (format "#ymlgen/include: file %s not found" path)
                            {})))
    result))
