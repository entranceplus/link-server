(set-env!
 :source-paths #{"src/clj" "src/cljc" "src/cljs"}
 :resource-paths #{"resources"}
 ;; :clean-targets ^{:protect false} [:target-path :compile-path "out/public/out"]
 :dependencies '[[clj-time "0.14.0"]
                 [cljs-ajax "0.7.2"]
                 [compojure "1.6.0"]
                 [conman "0.6.8"]
                 [honeysql "0.9.1"]
                 [cprop "0.1.11"]
                 [environ "1.1.0"]
                 [bk/ring-json "0.1.0"]
                 [boot-environ "1.1.0"]
                 [org.danielsz/system "0.4.1-SNAPSHOT"]
                 [funcool/struct "1.1.0"]
                 [luminus-immutant "0.2.3"]
                 [luminus-migrations "0.4.2"]
                 [luminus-nrepl "0.1.4"]
                 [org.clojure/clojure "1.9.0-alpha16" :scope "provided"]
                 [org.clojure/clojurescript "1.9.908" :scope "provided"]
                 [luminus/ring-ttl-session "0.3.2"]
                 [markdown-clj "1.0.1"]
                 [metosin/muuntaja "0.3.2"]
                 [metosin/ring-http-response "0.9.0"]
                 [crypto-password "0.2.0"]
                 [mount "0.1.11"]
                 [ring/ring-jetty-adapter "1.6.2"]
                 [org.postgresql/postgresql "42.1.4"]
                 [org.clojure/java.jdbc "0.7.1"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.webjars.bower/tether "1.4.0"]
                 [org.webjars/bootstrap "4.0.0-alpha.5"]
                 [org.webjars/font-awesome "4.7.0"]
                 [re-frame "0.10.1"]
                 [reagent "0.7.0"]
                 [crisptrutski/boot-cljs-test "0.3.2-SNAPSHOT" :scope "test"]
                 [orchestra "2017.08.13"]
                 [expound "0.3.0"]
                 [phrase "0.1-alpha1"]
                 [reagent-utils "0.2.1"]
                 [clj-http "3.7.0"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.6.2"]
                 [ring/ring-defaults "0.3.1"]
                 [secretary "1.2.3"]
                 [buddy/buddy-auth "2.1.0"]
                 [selmer "1.11.1"]
                 [prone "1.1.4"]
                 [ring/ring-mock "0.3.0"  :scope "test"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-devel "1.6.1"  :scope "test"]
                 [pjstadig/humane-test-output "0.8.2"  :scope "test"]
                 [binaryage/devtools "0.9.4"  :scope "test"]
                 [doo "0.1.7"  :scope "test"]
                 [adzerk/boot-test "1.2.0"]
                 [figwheel-sidecar "0.5.13"  :scope "test"]
                 [com.cemerick/piggieback "0.2.2" :scope "test"]
                 [weasel "0.7.0" :scope "test"  :scope "test"]
                 [org.clojure/tools.nrepl "0.2.13" :scope "test"]
                 [adzerk/boot-cljs "2.1.3" :scope "test"]
                 [adzerk/boot-cljs-repl "0.3.3" :scope "test"]
                 [pandeiro/boot-http "0.7.6" :scope "test"]
                 [powerlaces/boot-figreload "0.1.1-SNAPSHOT" :scope "test"]
                 [adzerk/boot-reload "0.5.2" :scope "test"]])

(require '[adzerk.boot-cljs :refer :all]
         '[adzerk.boot-cljs-repl :refer :all]
         '[adzerk.boot-reload :refer :all]
         '[links.systems :refer [dev-system prod-system]]
         '[environ.boot :refer [environ]]
         '[system.boot :refer [system run]]
         '[adzerk.boot-test :refer :all])

(deftask dev
  "Run a restartable system in the Repl"
  []
  (comp
   (environ :env (read-string (slurp "profiles.edn")))
   (watch :verbose true)
   (system :sys #'dev-system
           :auto true
           :files ["handler.clj" "db/core.clj" "domain.clj"])
   (reload)
   (cljs :source-map true :optimizations :none)
   (repl :server true)))

(deftask dev-clj
  []
  (comp
   (environ :env (read-string (slurp "profiles.edn")))
   (watch :verbose true)
   (system :sys #'dev-system :auto true :files ["handler.clj", "db/core.clj" "middleware.clj" "domain.clj"])
   (reload)
   (repl :server true)))

(deftask dev-run
  "Run a dev system from the command line"
  []
  (comp
   (environ :env (read-string (slurp "profiles.edn")))
   (cljs)
   (run :main-namespace "links.core" :arguments [#'dev-system])
   (wait)))

(deftask prod-run
  "Run a prod system from the command line"
  []
  (comp
   (environ :env {:http-port "8008"
                  :repl-port "8009"})
   (cljs :optimizations :advanced)
   (run :main-namespace "links.core" :arguments [#'prod-system])
(wait)))

(deftask wtest
  []
  (merge-env! :source-paths #{"test/clj"})
  (comp
   (environ :env (read-string (slurp "profiles.edn")))
   (watch :verbose true)
   (system :sys #'dev-system :auto true :files ["handler.clj" "db/core.clj"])
   (reload)
   (notify)
   (repl :server true)
   (test)
   (notify)))

(deftask uberjar
  "Builds an uberjar of this project that can be run with java -jar"
  []
  (comp
   (aot :namespace #{'links.core})
   (cljs :optimizations :advanced)
   (uber)
   (jar :file "links.jar" :main 'links.core)
   (sift :include #{#"links.jar"})
   (target)))
