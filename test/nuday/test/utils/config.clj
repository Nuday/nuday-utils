(ns nuday.test.utils.config
  (:require [clojure.test :refer :all]
            [environ.core :refer [env]]
            [nuday.utils.config :as cfg]))

(deftest get-str
  (testing "Config present"
    (with-redefs [env (constantly "some value")]
      (is (= "some value" (cfg/get-str :foo "no value")))))

  (testing "Use default when config absent"
    (with-redefs [env (constantly nil)]
      (is (= "no value" (cfg/get-str :foo "no value"))))))

(deftest get-bool
  (testing "True is true"
    (with-redefs [env (constantly "true")]
      (is (true? (cfg/get-bool :foo false)))))

  (testing "TRUE is true"
    (with-redefs [env (constantly "TRUE")]
      (is (true? (cfg/get-bool :foo false)))))

  (testing "False is false"
    (with-redefs [env (constantly "false")]
      (is (false? (cfg/get-bool :foo true)))))

  (testing "Something random is false"
    (with-redefs [env (constantly "what-the-what?")]
      (is (false? (cfg/get-bool :foo true)))))

  (testing "Use default when config absent"
    (with-redefs [env (constantly nil)]
      (is (true? (cfg/get-bool :foo true))))))

(deftest get-int
  (testing "Config present"
    (with-redefs [env (constantly "42")]
      (is (= 42 (cfg/get-int :foo 0)))))

  (testing "Use default when config absent"
    (with-redefs [env (constantly nil)]
      (is (= 0 (cfg/get-str :foo 0))))))

(defn- float= [a b]
  (< (Math/abs (- a b)) 0.001))

(deftest get-float
  (testing "Normal float"
    (with-redefs [env (constantly "42.24")]
      (is (float= 42.24 (cfg/get-float :foo 0)))))

  (testing "Whole number"
    (with-redefs [env (constantly "42")]
      (is (float= 42.0 (cfg/get-float :foo 0)))
      (is (float? (cfg/get-float :foo 0)))))

  (testing "Use default when config absent"
    (with-redefs [env (constantly nil)]
      (is (float= 92.29 (cfg/get-float :foo 92.29))))))
