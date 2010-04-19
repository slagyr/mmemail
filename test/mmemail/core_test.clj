(ns mmemail.core-test
  (:use clojure.contrib.test-is
    (mmemail
      [core] 
      [helper :only (deliver-message)])))

(defn mock-deliver-message [message]
  (def delivered-message message))

(use-fixtures :each
  (fn [f]
    (binding [deliver-message mock-deliver-message]
      (def delivered-message nil)
      (f))))

(deftest deliver-email-with-all-params
  (send-email {:host "acme.com" :port "1234" :user "Wiley" :to "joe@acme.com" :from "jill@acme.com" :subject "Hiya" :text ":)"})
  (is (not (nil? delivered-message)))
  (is (= "Hiya" (.getSubject delivered-message)))
  (is (= ":)" (.getContent delivered-message)))
  (is (= ["jill@acme.com"] (map #(.getAddress %1) (.getFrom delivered-message))))
  (is (= ["joe@acme.com"] (map #(.getAddress %1) (.getRecipients delivered-message javax.mail.Message$RecipientType/TO))))
  (is (= [] (map #(.getAddress %1) (.getRecipients delivered-message javax.mail.Message$RecipientType/CC))))
  (is (= [] (map #(.getAddress %1) (.getRecipients delivered-message javax.mail.Message$RecipientType/BCC)))))

(deftest create-mailer-function
  (let [mailer (create-mailer {:host "acme.com" :port "1234" :user "Wiley"})]
    (mailer {:to "joe@acme.com" :from "jill@acme.com" :subject "Hiya" :text ":)"})
    (is (not (nil? delivered-message)))
    (is (= "Hiya" (.getSubject delivered-message)))
    (is (= ":)" (.getContent delivered-message)))
    (is (= ["jill@acme.com"] (map #(.getAddress %1) (.getFrom delivered-message))))
    (is (= ["joe@acme.com"] (map #(.getAddress %1) (.getRecipients delivered-message javax.mail.Message$RecipientType/TO))))))


(clojure.contrib.test-is/run-tests)