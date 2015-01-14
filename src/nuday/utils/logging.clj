(ns nuday.utils.logging
  (:require [clojure.tools.logging :as log]))

(def log-time?
  "If true **at compile time**, enables logging of elapsed time."
  (Boolean/getBoolean "nuday.utils.logging.logTime"))

(defmacro log-time
  "Evaluates the body, logs the time it took by appending \"in %d ms\" to the
  specified msg-prefix, then returns the result of evaluating the body."
  [msg-prefix & body]
  (if log-time?
    `(let [msg-prefix# ~msg-prefix
           start-ts# (System/currentTimeMillis)
           retval# (do ~@body)
           elapsed-time# (- (System/currentTimeMillis) start-ts#)]
       (log/debugf "%s in %d ms" msg-prefix# elapsed-time#)
       retval#)
    `(do ~@body)))
