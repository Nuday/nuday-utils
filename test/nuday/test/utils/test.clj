(ns nuday.test.utils.test
  (:require [clojure.test :refer :all]
            [nuday.utils.test :as t]))

(deftest float=
  (testing "Within tolerance"
    (is (t/float= 0.0001 0.0002 0.1)))

  (testing "Outside tolerance"
    (is (not (t/float= 0.1 0.2 0.001))))

  (testing "Tolerance defaults to 0.01"
    (is (t/float= 0.01 0.02))
    (is (not (t/float= 0.01 0.021))))

  (testing "False when either number is nil"
    (is (not (t/float= nil 0.1)))
    (is (not (t/float= 0.1 nil)))))

(deftest with-mocks
  (testing "Mock a single function"
    (is (thrown-with-msg?
         RuntimeException #"^nope$"
         (t/with-mocks
           [#'identity]
           (fn [& _] (throw (RuntimeException. "nope")))
           #(identity 1)))))

  (testing "Pass args to test-fn"
    (is (thrown-with-msg?
         RuntimeException #"^still no$"
         (t/with-mocks
           [#'identity]
           (fn [& [msg]] (throw (RuntimeException. msg)))
           #(identity %) "still no"))))

  (testing "Mock a list of functions"
    (let [total (atom 0)
          no-change (fn [n] (swap! total + n) (swap! total - n))]
      (dotimes [_ 2] (no-change 2))
      (is (= 0 @total))

      (t/with-mocks
        [#'+ #'-]
        (fn [& _] 1)
        #(no-change %) 2)
      (is (= 1 @total))))

  (testing "Example"
    (defn foo [a] (inc a))
    (defn bar [a] (* a 2))

    (let [foobar (fn [a b]
                   (try (+ (foo a) (bar b))
                        (catch Exception _ 1)))]
      (t/with-mocks
        [#'foo #'bar]
        (fn [& _] (throw (RuntimeException. \"\")))
        (fn [a b] (is (= 1 (foobar a b)))) 1 2))))

(deftest make-question
  (testing "Poser 50-50 on AC/DC with links"
    (let [q (t/make-question 1 123 "Foo?" "Full answer"
                             {:link_wikipedia "http://en.wikipedia.org/wiki/Prisoners_in_Paradise"
                              :link_spotify "http://open.spotify.com/track/0pPfvUP6IpkCvZRbwtgBSZ"}
                             :poser :fifty-fifty :ac-dc)]
      (is (= (:game_round q) 1))
      (is (= (:question_id q) 123))
      (is (= (:question q) "Foo?"))
      (is (= (:alternative1 q) "123 1"))
      (is (= (:alternative2 q) "123 2"))
      (is (= (:alternative3 q) "123 3"))
      (is (= (:alternative4 q) "123 4"))
      (is (= (:full_Answer q) "Full answer"))
      (is (= (:link_wikipedia q) "http://en.wikipedia.org/wiki/Prisoners_in_Paradise"))
      (is (= (:link_spotify q) "http://open.spotify.com/track/0pPfvUP6IpkCvZRbwtgBSZ"))
      (is (= (:difficulty_id q) 1))
      (is (= (:difficulty q) "POSER"))
      (is (= (:earn_point q) 1))
      (is (= (:category_id q) 4))
      (is (= (:category_name q) "50/50"))
      (is (= (:theme_id q) 426))
      (is (= (:theme_name q) "AC/DC")))))

(deftest make-answer
  (testing "Make an answer"
    (let [q (t/make-question 1 123 "Foo?" "Full answer"
                             {:link_wikipedia "http://en.wikipedia.org/wiki/Prisoners_in_Paradise"
                              :link_spotify "http://open.spotify.com/track/0pPfvUP6IpkCvZRbwtgBSZ"}
                             :poser :fifty-fifty :ac-dc)
          a (t/make-answer q 456 3)]
      (is (= (:player_id a) 456))
      (is (= (:player_answer a) 3))
      (is (= (:question_id q) 123)))))

(deftest record-calls
  (testing "Record calls"
    (let [[+ args] (t/record-calls +)]
      (is (= 3 (+ 1 2)))
      (is (= 7 (+ 3 4)))
      (is (= [[1 2] [3 4]] @args)))))

(deftest get-and-increment
  (testing "Increment"
    (let [producer (t/get-and-increment 1000 60)]
      (is (= 1000 (producer 123)))
      (is (= 1060 (producer 4 5 6)))
      (is (= 1120 (producer))))))
