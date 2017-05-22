(ns nuday.utils.types
  (:require [clojure.string :refer [blank?]]
            [clojure.java.io :refer [resource]]
            [schema.core :as s]
            [instaparse.core :as instaparse]))

(def ^:private uuid?
  (partial re-matches
           #"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"))

(def ^:private url-parser
  (instaparse/parser (resource "url.abnf") :input-format :abnf))

(def ^:private email-parser
  (instaparse/parser (resource "email.abnf") :input-format :abnf))

(defn- url? [url]
  (-> url
      url-parser
      instaparse/failure?
      not))

(defn- email? [email]
  (-> email
      email-parser
      instaparse/failure?
      not))

(def URL (s/pred url? 'URL))

(def Email (s/pred email? 'email))

(def Map {s/Keyword s/Any})

(def NonEmptyStr (s/constrained s/Str (complement blank?) 'filled-str?))

(def Truthy s/Any)

(def UuidStr (s/constrained s/Str uuid? 'uuid?))

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
