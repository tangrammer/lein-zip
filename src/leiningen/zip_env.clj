(ns leiningen.zip-env
  (:require [clojure.java.io :refer [input-stream copy] :as io]
            [leiningen.core.main :refer [info]]
            [clojure.set :as s]
            [clojure.string :as st]
            )
  (:import [java.io FileOutputStream]
           [java.util.zip ZipOutputStream ZipEntry]))

(defn update-paths-with-dir-contents [paths]
  (vec (flatten (for [fs paths]
                  (filter (complement nil?)
                          (map #(when-not (.isDirectory %)
                                  (.getPath %))
                               (file-seq (io/file fs))))))))

(defn adapt-env [env s]
  (let [arr (st/split s #"\." )]
    (if (> (count arr) 1)
      (str (first arr) "-" env "." (last arr))
      (str (first arr) "-" env )
      )))

(defn adapt-fs [env fs env-files]
  (let [dynamic-env-files (set (map (partial adapt-env env) env-files ))]
    (vec (apply conj (clojure.set/difference (set fs) (set env-files)) dynamic-env-files))))

(defn zip-env
  "Zips files from :zip in project.clj
  to target/project-version.zip"
  [{v :version n :name fs :zip env-files :zip-env-files } & args]
  (assert (> 2 (count args)) "ony one argument as args can be passed representing env")
  (let [env (first args)
]
    [env v n fs env-files]

    (with-open [out (-> (str "target/" n "-" v ".zip")
                        (FileOutputStream.)
                        (ZipOutputStream.))]
      (doseq [f (update-paths-with-dir-contents fs)]
        (with-open [in (input-stream (if (and (contains? (set env-files) f) env)
                                       (adapt-env env f)
                                       f))]
          (.putNextEntry out (ZipEntry. f))
          (copy in out)
          (.closeEntry out)
          (info "zipped file" f))))))

#_(comment
(boolean "true")
  (zip-env {:version "1" :name "hola" :zip ["project.clj" "lib"] :zip-env-files ["project.clj"]} )
  (io/delete-file "target/hola-1.zip"))
