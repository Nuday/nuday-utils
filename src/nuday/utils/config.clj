(ns nuday.utils.config
  (:require [environ.core :refer [env]]))

(defn get-str
  [prop-name default]
  (-> (env prop-name)
      (or default)))

(defn get-bool
  [prop-name default]
  (Boolean/valueOf (get-str prop-name (str default))))

(defn get-int
  [prop-name default]
  (Integer/valueOf (get-str prop-name (str default))))

(defn get-long
  [prop-name default]
  (Long/valueOf (get-str prop-name (str default))))

(defn get-float
  [prop-name default]
  (Float/valueOf (get-str prop-name (str default))))
