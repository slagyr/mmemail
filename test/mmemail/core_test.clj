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

(deftest send-email-with-all-params
  (send-email {:host "acme.com" :port "1234" :user "Wiley" :to "joe@acme.com" :from "jill@acme.com" :subject "Hiya" :body ":)"})
  (is (not (nil? delivered-message)))
  (is (= "Hiya" (.getSubject delivered-message)))
  (is (= ":)" (.getContent delivered-message)))
  (is (= ["jill@acme.com"] (map #(.getAddress %1) (.getFrom delivered-message))))
  (is (= ["joe@acme.com"] (map #(.getAddress %1) (.getRecipients delivered-message javax.mail.Message$RecipientType/TO))))
  (is (= [] (map #(.getAddress %1) (.getRecipients delivered-message javax.mail.Message$RecipientType/CC))))
  (is (= [] (map #(.getAddress %1) (.getRecipients delivered-message javax.mail.Message$RecipientType/BCC)))))

(deftest send-email-uses-user-if-from-missing
  (send-email {:host "acme.com" :port "1234" :user "wiley@acme.com" :to "joe@acme.com" :subject "Hiya" :body ":)"})
  (is (= ["wiley@acme.com"] (map #(.getAddress %1) (.getFrom delivered-message)))))

(deftest create-mailer-function
  (let [mailer (create-mailer {:host "acme.com" :port "1234" :user "Wiley"})]
    (mailer {:to "joe@acme.com" :from "jill@acme.com" :subject "Hiya" :body ":)"})
    (is (not (nil? delivered-message)))
    (is (= "Hiya" (.getSubject delivered-message)))
    (is (= ":)" (.getContent delivered-message)))
    (is (= ["jill@acme.com"] (map #(.getAddress %1) (.getFrom delivered-message))))
    (is (= ["joe@acme.com"] (map #(.getAddress %1) (.getRecipients delivered-message javax.mail.Message$RecipientType/TO))))))

(deftest create-mailer-function-defaults-from-to-user
  (let [mailer (create-mailer {:host "acme.com" :port "1234" :user "wiley@acme.com"})]
    (mailer {:to "joe@acme.com" :subject "Hiya" :body ":)"})
    (is (= ["wiley@acme.com"] (map #(.getAddress %1) (.getFrom delivered-message))))))

(deftest create-mailer-function-can-store-defaults
  (let [mailer (create-mailer {:host "acme.com" :port "1234" :user "Wiley" :from "jill@acme.com" :subject "Hiya" :body ":)"})]
    (mailer {:to "joe@acme.com"})
    (is (not (nil? delivered-message)))
    (is (= "Hiya" (.getSubject delivered-message)))
    (is (= ":)" (.getContent delivered-message)))
    (is (= ["jill@acme.com"] (map #(.getAddress %1) (.getFrom delivered-message))))
    (is (= ["joe@acme.com"] (map #(.getAddress %1) (.getRecipients delivered-message javax.mail.Message$RecipientType/TO))))))


(clojure.contrib.test-is/run-tests)