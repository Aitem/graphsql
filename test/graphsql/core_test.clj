(ns graphsql.core-test
  (:require [clojure.test :refer :all]
            [clj-pg.honey :as db]
            [honeysql.core :as hsql]
            [graphsql.fixtures :as f]
            [clojure.java.jdbc :as jdbc]
            [graphsql.core :as sut]
            [matcho.core :refer [match]]))

(use-fixtures :once f/graphsql-fixture)

(def execute
  (comp #(jdbc/query  f/d %) sut/format))

(deftest grapsql
  (testing "Init DB"
    (match
     (db/query f/d "select 1 as select")
     [{:select 1}]))

  (testing "Simply query"
    (match
     (execute {:select :employee})
     [{:employee {:row {:id 1}}}
      {:employee {:row {:id 2}}}
      {:employee {:row {:id 3}}}
      {:employee {:row {:id 4}}}
      ]))

  (testing "Simply with query"
    (match
     (execute {:select :employee
               :with {:dpt {:select :department
                            :where [:= :department.id :employee.department ]}}})

     [{:employee {:row {:id 1}
                  :dpt {:row {:id 1 :name "IT"}}}}
      {:employee {:row {:id 2}
                  :dpt {:row {:id 2}}}}
      {:employee {:row {:id 3}
                  :dpt {:row {:id 1}}}}
      {:employee {:row {:id 4}
                  :dpt {:row {:id 3}}}}
      ]))

  )

(comment
  (f/preload)
  (execute {:select :employee})

  (jdbc/query  f/d ["SELECT true"])

  (println f/d)
  (db/query f/d "select * from \"user\"  "))
