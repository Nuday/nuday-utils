(ns nuday.test.utils.maps
  (:require [clojure.test :refer :all]
            [nuday.utils.maps :as maps]))

(deftest assoc-if
  (testing "Associate when truthy"
    (is (= {:foo 1, :bar 2}
           (maps/assoc-if {:foo 1} :bar 2))))

  (testing "Do not associate when falsy"
    (is (= {:foo 1}
           (maps/assoc-if {:foo 1} :bar nil))))

  (testing "Disassociate when falsy"
    (is (= {:foo 1}
           (maps/assoc-if {:foo 1, :bar 2} :bar nil)))))

(deftest dissoc-if-nil
  (testing "Disassociate when nil"
    (is (= {:bar 2}
           (maps/dissoc-if-nil {:foo nil, :bar 2} :foo))))

  (testing "Do not disassociate when not nil"
    (is (= {:foo 1, :bar 2}
           (maps/dissoc-if-nil {:foo 1, :bar 2} :foo)))
    (is (= {:foo false, :bar 2}
           (maps/dissoc-if-nil {:foo false, :bar 2} :foo)))))

(deftest update-if
  (testing "Update when not nil"
    (is (= {:foo 1, :bar 2}
           (maps/update-if {:foo 1, :bar 1} [:bar] inc)))
    (is (= {:foo 1, :bar "false"}
           (maps/update-if {:foo 1, :bar false} [:bar] str))))

  (testing "Do not update when nil"
    (is (= {:foo 1}
           (maps/update-if {:foo 1} [:bar] inc)))
    (is (= {:foo 1, :bar nil}
           (maps/update-if {:foo 1, :bar nil} [:bar] inc))))

  (testing "Nested update"
    (is (= {:foo {:bar 2}}
           (maps/update-if {:foo {:bar 1}} [:foo :bar] inc)))
    (is (= {:foo {:bar 1}}
           (maps/update-if {:foo {:bar 1}} [:foo :baz] inc))))

  (testing "Update f args style"
    (is (= {:foo 3}
           (maps/update-if {:foo 1} [:foo] + 2)))
    (is (= {:foo {:bar 3}}
           (maps/update-if {:foo {:bar 1}} [:foo :bar] + 2)))
    (is (= {:foo {:bar 1}}
           (maps/update-if {:foo {:bar 1}} [:foo :baz] + 2)))))

(deftest mfilter
  (testing "Empty input"
    (is (= nil (maps/mfilter (constantly true) nil)))
    (is (= {} (maps/mfilter (constantly false) {}))))

  (testing "Filter on keys"
    (is (= {:foo 1, :baz 3}
           (maps/mfilter (fn [k v] (not= :bar k)) {:foo 1, :bar 2, :baz 3}))))

  (testing "Filter on values"
    (is (= {:foo 1, :baz 3}
           (maps/mfilter (fn [k v] (not (nil? v))) {:foo 1, :bar nil, :baz 3})))))

(deftest mmap
  (testing "Transform keys"
    (is (= {"foo" 1, "bar" 2, "baz" 3}
           (maps/mmap #(vector (name %1) %2)
                      {:foo 1, :bar 2, :baz 3}))))

  (testing "Transform vals"
    (is (= {:foo "1", :bar "2", :baz "3"}
           (maps/mmap #(vector %1 (str %2))
                      {:foo 1, :bar 2, :baz 3}))))

  (testing "Transform keys and vals"
    (is (= {"foo" "2", "bar" "3", "baz" "4"}
           (maps/mmap #(vector (name %1) (str (inc %2)))
                      {:foo 1, :bar 2, :baz 3}))))

  (testing "Transform collection"
    (is (=  {1 {:id 1}, 2 {:id 2}}
           (maps/mmap #(vector (:id %) %)
                      [{:id 1} {:id 2}]))))

  (testing "No input"
    (is (nil? (maps/mmap (constantly [:foo "bar"]) nil)))))

(deftest rename-keys
  (testing "Rename keys with a function"
    (is (= {"foo" 1, "bar" 2}
           (maps/rename-keys {:foo 1, :bar 2} name))))

  (testing "Rename keys with a keymap"
    (is (= {:aardvark 1, :zebra 2}
           (maps/rename-keys {:foo 1, :bar 2} {:foo :aardvark, :bar :zebra})))))

(deftest replace-nils
  (testing "No nils, no replacements"
    (is (= {:foo 1, :bar "two"}
           (maps/replace-nils {:foo 1, :bar "two"} [:foo :bar] ""))))

  (testing "Key not specified, no replacement"
    (is (= {:foo 1, :bar nil}
           (maps/replace-nils {:foo 1, :bar nil} [:foo] ""))))

  (testing "All specified keys replaced"
    (is (= {:foo "", :bar ""}
           (maps/replace-nils {:foo nil, :bar nil} [:foo :bar] "")))
    (is (= {:foo "", :bar nil}
           (maps/replace-nils {:foo nil, :bar nil} [:foo] "")))))

(deftest dissoc-nils
  (testing "No map"
    (is (nil? (maps/dissoc-nils nil))))

  (testing "Empty map"
    (is (= {} (maps/dissoc-nils {}))))

  (testing "No nils"
    (is (= {:foo 1, :bar 2, :baz 3}
           (maps/dissoc-nils {:foo 1, :bar 2, :baz 3}))))

  (testing "Some nils"
    (is (= {:foo 1}
           (maps/dissoc-nils {:foo 1, :bar nil, :baz nil}))))

  (testing "All nils"
    (is (= {}
           (maps/dissoc-nils {:foo nil, :bar nil, :baz nil})))))
