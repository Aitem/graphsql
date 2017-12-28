(ns graphsql.db
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clj-pg.pool :as pool]
            [clj-pg.honey :as db]
            [graphsql.core :refer :all]))


(defonce datasources (atom {}))

(def default-db "graphsql")
(def db-url "jdbc:postgresql://localhost:5480/graphsql?user=postgres&password=postgres&stringtype=unspecified")

(defn stop [datasources]
  (doseq [[nm {conn :datasource}] @datasources]
    (pool/close-pool conn))
  (reset! datasources {}))

(defn shutdown-connections []
  (stop datasources))

(defn ds [database-url db-name]
  (if-let [ds (get @datasources db-name)]
    ds
    (let [ds-opts {:idle-timeout       1000
                   :minimum-idle       0
                   :connection-timeout 15000
                   :maximum-pool-size  2
                   :connection-init-sql "select 1"
                   :data-source.url   database-url}
          ds (pool/create-pool ds-opts)
          pool {:datasource ds}]
      (swap! datasources assoc db-name pool)
      pool)))


(defn get-db [& [nm]]
  (ds db-url (or nm default-db)))

(defn ensure-db []
  (let [db (get-db :postgres)]
    (when-not (db/database-exists? db default-db)
      (db/create-database db default-db))))
