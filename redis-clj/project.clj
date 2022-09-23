(defproject redis "0.1.0-SNAPSHOT"
  :description "Redis implemented in Clojure"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/core.async "1.5.648"]]
  :main ^:skip-aot redis.core
  :repl-options {:init-ns redis.core})
