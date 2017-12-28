(defproject graphsql "0.1.0-graphsql"
  :description "GraphQL for Postgres based on honeysql"
  :url "https://github.com/Aitem/graphsql"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.9.0-alpha16"]
                 [honeysql "0.9.1"]]
  :source-paths ["src"]

  :profiles {:dev {:source-paths ["src" "test"]
                   :dependencies [[matcho "0.1.0-RC6"]
                                  [clj-pg "0.0.3"]
                                  [ring/ring-defaults "0.2.3"]
                                  [ring "1.5.1"]
                                  [org.postgresql/postgresql "9.4.1211.jre7"]
                                  [org.clojure/java.jdbc "0.6.1"]]}})
