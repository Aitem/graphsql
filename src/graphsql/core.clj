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

(defn query-entry [select without]
  (let [res  (str "row_to_json(" (name select) ".*)::jsonb")
        dissoc-q (dissoc-query without)]
    (hsql/raw (str "(" res dissoc-q ")"))))

(defn format-query [{:keys [with select collection query without] :as gsql}]
  (let [without (concat (keys with) without)
        columns [[(query-entry select without) :row]]
        columns (if with
                  (reduce-kv (fn [acc as q] (conj acc [(format-query q) as])) columns with)
                  columns)

        as (-> select name (str "_subselect") keyword)
        as_columns (-> as name (str ".*") keyword)
        agg (hsql/call :row_to_json as_columns)
        agg (if collection (hsql/call :json_agg agg) agg)

        conditions  (dissoc gsql :select :with :collection :search :without)]
    (merge
     {:select [[agg select]]
      :from [[(merge
               {:select columns
                :from [select]}
               conditions)
              as]]}
     query)))


(defn format [gsql]
  (->> gsql
       format-query
       hsql/format))
