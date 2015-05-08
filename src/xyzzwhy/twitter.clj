(ns xyzzwhy.twitter
  (:use
    [twitter.oauth]
    [twitter.api.restful])
  (:require [environ.core :refer [env]])
  (:import (twitter.callbacks.protocols SyncSingleCallback)))

(def credentials
  (make-oauth-creds
    (env :xyzzwhy-twitter-consumer-key)
    (env :xyzzwhy-twitter-consumer-secret)
    (env :xyzzwhy-twitter-user-access-token)
    (env :xyzzwhy-twitter-user-access-token-secret)))

(defn post-to-twitter 
  "This, uh, posts to Twitter."
  [status-text]
  (statuses-update :oauth-creds credentials :params {:status status-text}))
