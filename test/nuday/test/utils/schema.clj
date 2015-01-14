(ns nuday.test.utils.schema
  (:require [clojure.test :refer :all]
            [nuday.utils.schema :as schema]
            [schema.core :as s]))

(deftest select-schema
  (testing "Only required keys"
    (let [schema {:foo s/Str, :bar s/Int}]
      (is (= {:foo "Yup", :bar 42} (schema/select-schema {:foo "Yup", :bar 42, :baz true} schema)))
      (is (= {:foo "Yup", :bar 42} (schema/select-schema {:foo "Yup", :bar 42} schema)))
      (is (= {:foo "Yup"} (schema/select-schema {:foo "Yup"} schema)))))

  (testing "Optional key"
    (let [schema {:foo s/Str, (s/optional-key :bar) s/Int}]
      (is (= {:foo "Yup", :bar 42} (schema/select-schema {:foo "Yup", :bar 42, :baz true} schema)))
      (is (= {:foo "Yup", :bar 42} (schema/select-schema {:foo "Yup", :bar 42} schema)))
      (is (= {:foo "Yup"} (schema/select-schema {:foo "Yup"} schema)))))

  (testing "Required key and wildcard"
    (let [schema {:foo s/Str, s/Keyword s/Any}]
      (is (= {:foo "Yup", :bar 42, :baz true} (schema/select-schema {:foo "Yup", :bar 42, :baz true} schema)))
      (is (= {:foo "Yup", :bar 42} (schema/select-schema {:foo "Yup", :bar 42} schema)))
      (is (= {:foo "Yup"} (schema/select-schema {:foo "Yup"} schema))))))
