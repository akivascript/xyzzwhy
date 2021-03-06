(ns xyzzwhy.bot
  (:gen-class)
  (:require [clj-time.local :as local]
            [com.stuartsierra.component :as component]
            [xyzzwhy
             [engine :as e]
             [text :refer :all]
             [twitter :as t]]))

(defn- start-bot
  "Initializes Xyzzwhy and starts the bot tweeting."
  [source]
  (let [interrupt (atom false)
        bot (future (e/assign-corpus source)
                    (while (not @interrupt)
                      (let [interval (+ 1200000 (rand-int 1200000))
                            tweet (-> (e/get-tweet) :text)]
                        (t/post-to-twitter tweet)

                        (println "--")
                        (println tweet)
                        (println "On:" (local/format-local-time (local/local-now) :rfc822))
                        (println "Pausing:" (int (/ interval 60000)) "minutes")
                        (println "--")

                        (try
                          (Thread/sleep interval)
                          (catch InterruptedException e
                            (reset! interrupt true))))))]
    (println "xyzzwhy is ready for some magical adventures!")
    (println "Started at:" (local/format-local-time (local/local-now) :rfc822))
    bot))

(defrecord Xyzzwhy [source]
  component/Lifecycle
  (start [component]
    (if (:bot component)
      component
      (assoc component :bot (start-bot source))))
  (stop [component]
    (if (:bot component)
      (future-cancel (:bot component))
      component)))

(defn new-bot
  "Initializes an instance of an Xyzzwhy record."
  [source]
  (map->Xyzzwhy {:source source}))

(defn xyzzwhy
  "System-level controller for xyzzwhy for use with
  Component."
  ([]
   (xyzzwhy []))
  ([config]
   (let [{:keys [source kind]
          :or {source 'xyzzwhy.text
               kind :namespace}} config]
     (component/system-map
      :source source
      :kind type
      :app (component/using
            (new-bot source)
            [:source :kind])))))

(defn -main
  [& args]
  (let [{:keys [source kind]
         :or {source 'xyzzwhy.text
              kind :namespace}} args]
    (component/start
     (xyzzwhy {:source source :kind kind}))))
