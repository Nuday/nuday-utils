(defproject nuday-utils "0.1.0-SNAPSHOT" ; handled by lein-git-version
  :description "Nuday Clojure utilities"
  :url "https://github.com/Nuday/nuday-utils"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [environ "1.1.0"]
                 [prismatic/schema "1.1.4"]
                 [instaparse "1.4.5"]]

  :resource-paths ["resources"]

  :plugins [[org.clojars.cvillecsteele/lein-git-version "1.0.3"]]

  :profiles {:dev {:dependencies [[pjstadig/humane-test-output "0.8.1"]]
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]
                   :jvm-opts ["-Dnuday.utils.logging.logTime=true"]
                   :source-paths ["src" "dev"]}

             :test {;; dev/user.clj breaks the build in test mode
                    :source-paths ^:replace ["src"]}})
