(ns graphsql.core
  (:require [honeysql.core :as hsql]
            [clojure.string :as str]))

(defn dissoc-query [without]
  (when without
    (->> without
         (map (comp #(format " #- '{%s}'" %)
                    #(str/replace % #"\." ",")
                    name))
         str/join)))

(defn query-entry [without]
  (let [res  "(resource || jsonb_build_object('id', id , 'resourceType', resource_type))"
        dissoc-q (dissoc-query without)]
    (hsql/raw (str res dissoc-q))))

(defn format-query [{:keys [with select collection query without] :as gsql}]
  (let [columns [:*
                 #_[(query-entry without)   :resource]]
        columns (if with
                  (reduce-kv (fn [acc as q] (conj acc [(format-query q) as])) columns with)
                  columns)
        as (-> select name (str "_subselect") keyword)
        as_columns (-> as name (str ".*") keyword)
        agg (if collection
              (hsql/call :json_agg (hsql/call :row_to_json as_columns))
              (hsql/call :row_to_json as_columns))
        conditions  (dissoc gsql :select :with :collection :search :without)]
    (merge
     {:select [[agg select]]
      :from [[(merge {:select columns
                      :from [select]}
                     conditions)
              as]]}
     query)))


(defn format [gsql]
  (->> gsql
       format-query
       hsql/format))
