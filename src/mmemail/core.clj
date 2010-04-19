(ns mmemail.core
  (:use mmemail.helper))

(defn send-email [params]
  (let [session (create-session params)
        message (create-message session params)]
    (deliver-message message)))

(defn create-mailer [params]
  (let [session (create-session params)]
    (fn [details]
      (deliver-message (create-message session (merge params details))))))

