(ns mini-java.core
  (:require [mini-java.parser  :as    parser]
            [mini-java.static-semantics :as static-semantics]
            [rhizome.viz       :as    rhizome]
            [clojure.tools.cli :as    cli]
            [clojure.pprint    :refer [pprint]])
  (:gen-class))

(def cli-options
  [[nil "--syntax-only"
    "Only do syntax checking"]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Usage: mini-javac [options] action"
        ""
        "Options:"
        options-summary]
       (clojure.string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (clojure.string/join \newline errors)))

(defn exit
  ([]
     (System/exit 0))
  ([status]
     (System/exit status))
  ([status msg]
     (println msg)
     (System/exit status)))

(defn -main
  ([& args]
     (let [{:keys [options arguments errors summary]}
           (cli/parse-opts args cli-options)]
       (cond
        (:help options) (exit 0 (usage summary))
        (not= (count arguments) 1) (exit 1 (usage summary))
        errors (exit 1 (error-msg errors)))
       (let [source-file (first arguments)
             ast (parser/mini-java source-file)]
         (when (:syntax-only options) (exit 0))
         (let [class-table (static-semantics/class-table ast)]
           (println "AST:")
           (pprint ast)
           (println)
           (println "CLASS TABLE:")
           (pprint class-table))))
     nil))
