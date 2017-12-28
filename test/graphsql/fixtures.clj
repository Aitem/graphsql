(ns graphsql.fixtures
  (:require  [clojure.test :as t]
             [graphsql.db :refer [ensure-db get-db]]
             [clj-pg.honey :as db]))

(def d (do (ensure-db) (get-db)))


(defn truncate []
  (db/exec! d "drop table employee ")
  (db/exec! d "drop table department ")
  )

(defn preload []
  (db/exec! d "
create table if not exists employee(
id integer,
email varchar(40),
birthdate timestamp with time zone,
data  jsonb,
department integer
)")

  (db/exec! d "
create table if not exists department(
id integer,
name varchar(40),
location integer,
data  jsonb
)")

  (db/exec! d "
insert into employee (id, email, birthdate, data, department)
values
(1, 'foo@user.com', '2001-10-19'::date , '{\"gender\": \"male\"}', 1),
(2, 'bar@user.com', '2002-10-19'::date , '{\"gender\": \"male\"}', 2),
(3, 'mar@user.com', '2003-10-19'::date , '{\"gender\": \"female\"}', 1),
(4, 'tar@user.com', '2004-10-19'::date , '{\"gender\": \"female\"}', 3)
")

  (db/exec! d "
insert into department (id, name, data)
values
(1, 'IT',  '{\"floor\": 2}'),
(2, 'HR',  '{\"floor\": 3}'),
(3, 'STOCK',  '{\"floor\": 1}')
")

  )

(defn graphsql-fixture [f]
  (preload)
  (f)
  (truncate))
