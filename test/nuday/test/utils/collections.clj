(ns nuday.test.utils.collections
  (:require [clojure.test :refer :all]
            [nuday.utils.collections :as util]))

(deftest empty-or-contains?
  (testing "coll must be nil or collection"
    (is (thrown? AssertionError (util/empty-or-contains? 1 :anything)))
    (is (thrown? AssertionError (util/empty-or-contains? "nope" :anything))))

  (testing "True if empty"
    (is (util/empty-or-contains? nil :anything))
    (is (util/empty-or-contains? [] :anything))
    (is (util/empty-or-contains? #{} :anything)))

  (testing "True if contains value"
    (is (util/empty-or-contains? [:anything] :anything))
    (is (util/empty-or-contains? #{:anything} :anything)))

  (testing "False if value missing"
    (is (not (util/empty-or-contains? [:anything] :something)))
    (is (not (util/empty-or-contains? #{:anything} :something))))

  (testing "True if coll is a collection of a collection"
    (is (util/empty-or-contains? [[:anything]] :anything))))

(deftest shuffle-with-seed
  (testing "coll must be nil or collection"
    (is (thrown? AssertionError (util/shuffle-with-seed 1)))
    (is (thrown? AssertionError (util/shuffle-with-seed "nope"))))

  (testing "No elements"
    (is (= [] (util/shuffle-with-seed []))))

  (testing "One element"
    (is (= [1] (util/shuffle-with-seed [1]))))

  (testing "Same seed, same result"
    (is (= (util/shuffle-with-seed (range 1000) 1234567)
           (util/shuffle-with-seed (range 1000) 1234567)))))

(deftest take-rand
  (testing "coll must be nil or collection"
    (is (thrown? AssertionError (util/take-rand 1 1)))
    (is (thrown? AssertionError (util/take-rand 1 "nope"))))

  (testing "Return no items when none requested"
    (is (empty? (util/take-rand 0 (range 5)))))

  (testing "Return all items if collection contains fewer than requested"
    (is (= 0 (count (util/take-rand 5 []))))
    (is (= 1 (count (util/take-rand 5 (range 1)))))
    (is (= 4 (count (util/take-rand 5 (range 4))))))

  (testing "Return randomly selected items"
    (let [items (util/take-rand 100 (range 100))]
      ;; The chance of randomly selecting 100 items in the input order is vanishingly small
      (is (not= (range 100) items))))

  (testing "Don't select a single item more than once"
    (let [items (util/take-rand 5 (range 5))]
      (is (= (count items) (count (apply hash-set items))))))

  (testing "Same seed yields same items"
    (let [seed (System/currentTimeMillis)
          take-5 (fn [] (util/take-rand 5 (range 5) seed))]
      (is (= (take-5) (take-5))))))
