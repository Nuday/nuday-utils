(ns nuday.utils.types
  (:require [clojure.java.io :refer [resource]]
            [schema.core :as s :refer [Any Keyword Str]]
            [instaparse.core :as instaparse]))

(def ^:private uuid?
  (partial re-matches
           #"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"))

(def ^:private url-parser
  (instaparse/parser (resource "url.abnf") :input-format :abnf))

(defn- url? [url]
  (-> url
      url-parser
      instaparse/failure?
      not))

(def URL (s/pred url? 'URL))

(def Map
  {Keyword Any})

(def NonEmptyStr
  (s/both Str
          (s/pred (comp not empty?) 'not-empty?)))

(def Truthy
  Any)

(def UuidStr
  (s/both Str
          (s/pred uuid? 'uuid?)))

(defn between?
  "Predicate that validates that the number of items in a collection or a scalar
number is between low and high, inclusive."
  [low high]
  (s/pred #(let [cnt (if (coll? %) (count %) %)]
             (and (>= cnt low) (<= cnt high)))
          (symbol (format "between-%d-and-%d?" low high))))

(defn eq?
  "Predicate that validates that the number of items in a collection or a scalar
number is equal to n."
  [n]
  (s/pred #(let [cnt (if (coll? %) (count %) %)]
             (= cnt n))
          (symbol (format "eq-%d?" n))))

(defn gt?
  "Predicate that validates that the number of items in a collection or a scalar
number is greater than n."
  [n]
  (s/pred #(let [cnt (if (coll? %) (count %) %)]
             (> cnt n))
          (symbol (format "gt-%d?" n))))

(defn lt?
  "Predicate that validates that the number of items in a collection or a scalar
number is less than n."
  [n]
  (s/pred #(let [cnt (if (coll? %) (count %) %)]
             (< cnt n))
          (symbol (format "lt-%d?" n))))
