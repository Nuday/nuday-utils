(ns nuday.utils.schema
  (:require [schema.core :as s]))

(defn or-validate
  "Validates that model m is either nil or conforms to the schema."
  [schema m]
  (or (nil? m)
      (s/validate schema m)))

(defn select-schema
  "Given a schema and a map m, selects only the keys of m that are named in the schema."
  [m schema]
  (let [wildcard? #(= s/Keyword %)
        ;; This should really be
        ;;
        ;;     optional? #(instance? schema.core.OptionalKey %)
        ;;
        ;; but some weirdness with defrecord is causing problems.
        ;; See: http://stackoverflow.com/questions/26383713
        optional? #(and (map? %) (contains? % :k))]
    (if (some wildcard? (keys schema))
      m  ; the schema allows any keyword as a key, so just return the map
      (let [ks (->> schema keys (map #(if (optional? %) (:k %) %)))]
        (select-keys m ks)))))
