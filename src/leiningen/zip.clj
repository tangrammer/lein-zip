(ns leiningen.zip
  (:require [clojure.java.io :refer [input-stream copy] :as io]
            [leiningen.core.main :refer [info]])
  (:import [java.io FileOutputStream]
           [java.util.zip ZipOutputStream ZipEntry]))

(defn update-paths-with-dir-contents [paths]
  (vec (flatten (for [fs paths]
                  (filter (complement nil?)
                          (map #(when-not (.isDirectory %)
                                  (.getPath %))
                               (file-seq (io/file fs))))))))


(defn zip
  "Zips files from :zip in project.clj
  to target/project-version.zip"
  [{v :version n :name fs :zip} & args]
  (with-open [out (-> (str "target/" n "-" v ".zip")
                      (FileOutputStream.)
                      (ZipOutputStream.))]
    (doseq [f (update-paths-with-dir-contents fs)]
      (with-open [in (input-stream f)]
        (.putNextEntry out (ZipEntry. f))
        (copy in out)
        (.closeEntry out)
        (info "zipped file" f)))))
