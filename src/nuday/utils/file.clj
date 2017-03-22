(ns nuday.utils.file
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn read-edn-resource
  "Tries to read a resource from classpath as EDN.
Throws exception if resource doesn't exist."
  [resource-name]
  (if-let [resource (io/resource resource-name)]
    (-> resource
        slurp
        edn/read-string)
    (throw (ex-info "Resource does not exist."
                    {:causes #{:resource-not-read}
                     :resource-name resource-name}))))
