(ns nuday.utils.collections)

(def nil-or-coll? #(or (nil? %) (coll? %)))

(defn empty-or-contains?
  "Returns true if coll is empty or contains value v."
  [coll v]
  {:pre [(nil-or-coll? coll)]}
  (let [coll (if-not (set? coll) (flatten coll) coll)]  ; flattening a set removes all items
    (or (empty? coll)
        (some #{v} coll))))

(defn shuffle-with-seed
  "Return a random permutation of coll with a seed.

  Thanks to [sloth](http://stackoverflow.com/a/24553447/58994)!"
  [coll & [seed]]
  {:pre [(nil-or-coll? coll)]}
  (if-not seed
    (shuffle coll)

    ;; else
    (let [al (java.util.ArrayList. coll)
          rnd (java.util.Random. seed)]
      (java.util.Collections/shuffle al rnd)
      (clojure.lang.RT/vector (.toArray al)))))

;; See http://stuartsierra.com/2015/04/26/clojure-donts-concat
(defn strict-concat [& lists]
  (loop [acc []
         [list & lists] lists]
    (if (and (nil? list) (nil? lists))
      acc
      (recur (into acc list) lists))))

(defn take-rand
  "Returns n randomly selected items from coll, or all items if there are fewer than n.
  No item will be picked more than once."
  [n coll & [seed]]
  {:pre [(nil-or-coll? coll)]}
  (take n (shuffle-with-seed coll seed)))

(defn take-rand-with-seed
  "Convenience function to call take-rand with a seed. Useful with the ->> macro."
  [n seed coll]
  (take-rand n coll seed))
