(defproject two-fifty-analytics "0.1.0-SNAPSHOT"
  :author "Anurag Ohri"
  :description "A storage/analytics provider for the game 250"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [http-kit "2.4.0"]
                 [compojure "1.6.2"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [org.postgresql/postgresql "42.2.14"]]
  :repl-options {:init-ns two-fifty-analytics.core}
  :uberjar-name "two-fifty-analytics-standalone.jar"
  :main two-fifty-analytics.core)
