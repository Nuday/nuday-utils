(ns nuday.utils.maps
  (:require [clojure.set]))

(defn assoc-if
  "Given a map m, key k, and value v, associates the key/value pair with the map
   if the value is truthy. **Else, disassociates the key from the map.**"
  [m k v]
  (if v
    (assoc m k v)
    (dissoc m k)))

(defn dissoc-if-nil
  "Given a map m, if the value of key k is nil, it will be disassociated from
  the map."
  [m k]
  (if (nil? (m k))
    (dissoc m k)
    m))

(defn update-if
  "Given a map m, if the value of key in key path ks is not nil, it will be updated
  with the function f (as per clojure.core/update-in)."
  [m ks f & args]
  (if-not (nil? (get-in m ks))
    (apply update-in m ks f args)
    m))

(defn mfilter
  "Filters keys and values of input data with function f and returns the result as a
  map. f is a function taking a key and value as arguments and returning a bool.

  *Examples*

      (maps/mfilter (fn [k v] (not= :foo k)) {:foo 1, :bar 2, :baz 3})
      ;=> {:bar 2, :baz 3}

      (maps/mfilter (fn [k v] (not (nil? v))) {:foo 1, :bar nil, :baz 3})
      ;=> {:foo 1, :baz 3}"
  [f input]
  (when input
    (reduce (fn [acc [k v]]
              (if (f k v)
                (assoc acc k v)
                acc))
            {}
            input)))

(defn mmap
  "Maps keys and values of input data with function f and returns the result as a
  map. f is a function taking either a key and value as arguments if input is a map,
  or just a value as its argument otherwise, and returning a vector of key and value.

  *Examples*

      (maps/mmap #(vector (name %1) %2) {:foo 1, :bar 2, :baz 3})
      ;=> {\"foo\" 1, \"bar\" 2, \"baz\" 3}
      
      (maps/mmap #(vector (:id %) %) [{:id 1} {:id 2}])
      ;=> {1 {:id 1}, 2 {:id 2}}"
  [f input]
  (when input
    (let [f (if (map? input)
              #(f (key %) (val %))
              #(f %))]
      (reduce #(conj %1 (f %2)) {} input))))

(defn rename-keys
  "Given a map m, and a function f, renames the keys in m by applying f to each
  existing key. If f is a map, clojure.set/rename-keys will be used instead."
  [m f]
  (if (map? f)
    (clojure.set/rename-keys m f)
    (mmap #(vector (f %1) %2) m)))

(defn replace-nils
  "Given a map m, all keys in ks with nil values will be replaced with value v."
  [m ks v]
  (reduce-kv (fn [acc k the-v]
               (if (nil? the-v)
                 (assoc acc k v)
                 acc))
             m
             (select-keys m ks)))

(def dissoc-nils
  "Given a map, removes every key/value pairs for which the value is nil."
  (partial mfilter (fn [_ v] (some? v))))
