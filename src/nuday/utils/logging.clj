(ns nuday.utils.logging
  (:require [clojure.tools.logging :as log]))

(def log-time?
  "If true **at compile time**, enables logging of elapsed time."
  (Boolean/getBoolean "nuday.utils.logging.logTime"))

(defmacro log-time-internal
  [msg-prefix pred & body]
  (if log-time?
    `(let [msg-prefix# ~msg-prefix
           start-ts# (System/currentTimeMillis)
           retval# (do ~@body)
           elapsed-time# (- (System/currentTimeMillis) start-ts#)]
       (when (~pred retval#)
         (log/debugf "%s in %d ms" msg-prefix# elapsed-time#))
       retval#)
    `(do ~@body)))

(defmacro log-time
  "Evaluates the body, logs the time it took by appending \"in %d ms\" to the
  specified msg-prefix, then returns the result of evaluating the body."
  [msg-prefix & body]
  `(log-time-internal ~msg-prefix (constantly true) ~@body))

(defmacro log-time-when
  "Evaluates the body, logs the time it took by appending \"in %d ms\" to the
  specified msg-prefix *if the body returns a truthy value*, then returns the
  result of evaluating the body."
  [msg-prefix pred & body]
  `(log-time-internal ~msg-prefix ~pred ~@body))
