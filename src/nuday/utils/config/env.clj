(ns nuday.utils.config.env
  (:require [environ.core :refer [env]]))

(defn get-str
  [prop-name default]
  (-> (env prop-name)
      (or default)))

(defn get-bool
  [prop-name default]
  (Boolean/valueOf (get-str prop-name default)))

(defn get-int
  [prop-name default]
  (Integer/valueOf (get-str prop-name default)))
