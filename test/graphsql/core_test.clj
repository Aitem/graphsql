(ns graphsql.core-test
  (:require [clojure.test :refer :all]
            [clj-pg.honey :as db]
            [graphsql.core :as sut]
            [matcho.core :refer [match]]
            [graphsql.db :refer [ensure-db get-db]]))

(def d (do (ensure-db) (get-db)))

(deftest grapsql
  (testing "Init DB"
    (match
     (db/query d "select 1 as select")
     [{:select 1}])))

