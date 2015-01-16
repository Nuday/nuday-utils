(ns nuday.utils.types
  (:require [schema.core :as s]))

(def ^:private uuid?
  (partial re-matches #"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"))

(def NonEmptyStr
  (s/both s/Str 
          (s/pred (comp not empty?) 'not-empty?)))

(def UuidStr
  (s/both s/Str
          (s/pred uuid? 'uuid?)))
