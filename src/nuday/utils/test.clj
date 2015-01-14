;; Useful utility functions for testing
(ns nuday.utils.test)

;; See http://gettingclojure.wikidot.com/cookbook:numbers#toc10
(defn float=
  "Returns true if the difference between floating-point numbers x and y is less than or
  equal to 0.01, or the value of the optional tolerance arg."
  [x y & [tolerance]]
  (and (not (nil? x))
       (not (nil? y))
       (<= (Math/abs (- x y)) (or tolerance 0.01))))

(defn with-mocks
  "Iterates through a list of functions to-mock, rebinding each to mock-fn, and calls
  test-fn with the optional test-args.

  Example:

      (defn foobar [a b]
        (try (+ (foo a) (bar b))
             (catch Exception _ 1)))

      (deftest test-foobar
        (testing \"Exceptions are handled\"

          (with-mocks
            [#'foo #'bar]
            (fn [& _] (throw (RuntimeException. \"\")))
            (fn [a b] (is (= 1 (foobar a b)))) 1 2)))"
  [to-mock mock-fn test-fn & test-args]
  (doseq [f to-mock]
    (let [real-fn @f]
      (try
        (alter-var-root f (constantly mock-fn))
        (apply test-fn test-args)
        (finally
          (alter-var-root f (constantly real-fn)))))))

(def question-fragments
  {:poser {:difficulty_id 1, :difficulty "POSER", :earn_point 1}
   :fan {:difficulty_id 2, :difficulty "FAN", :earn_point 2}
   :scientist {:difficulty_id 3, :difficulty "SCIENTIST", :earn_point 3}

   :fifty-fifty {:category_id 4, :category_name "50/50"}
   :rocker {:category_id 3, :category_name "ROCKER"}
   :song {:category_id 1, :category_name "SONG"}

   :ac-dc {:theme_id 426, :theme_name "AC/DC"}
   :alice-cooper {:theme_id 432, :theme_name "ALICE COOPER"}
   :dead-kennedys {:theme_id 471, :theme_name "DEAD KENNEDYS"}
   :five-finger-death-punch {:theme_id 494 :theme_name "FIVE FINGER DEATH PUNCH"}})

(defn make-question [round id q a links & fragments]
  (merge {:game_round round, :question_id id, :question q, :full_Answer a}
         links
         (->> (map question-fragments fragments) (apply merge))
         (->> [1 2 3 4]
              (map (fn [i] [(keyword (str "alternative" i)) (str id " " i)]))
              flatten
              (apply hash-map))))

(defn make-answer [question player answer]
  (merge {:player_id player, :player_answer answer}
         question))

(defn record-calls
  "Returns a function that calls f and an atom that will be used to record args
  passed with each call of f.

  *Example*

      (let [[+ args] (record-calls +)]
        (+ 1 2)  ;=> 3
        (+ 3 4)  ;=> 7
        @args    ;=> [[1 2] [3 4]]"
  [f]
  (let [register (atom [])]
    [(fn [& args]
        (swap! register conj args)
        (apply f args))
     register]))

(defn get-and-increment
  "Returns a function that generates an infinite sequence of values, starting with
  the specified start value and incrementing by step for each subsequent call.

  *Example*

      (def producer (get-and-increment 1000 60))
      (producer)  ;=> 1000
      (producer)  ;=> 1060
      (producer)  ;=> 1120"
  [start step]
  (let [next-val (atom start)]
    (fn [& _] (let [cur-val @next-val]
               (swap! next-val + step)
               cur-val))))
