(ns mmemail.core-test
  (:use clojure.contrib.test-is
        mmemail.core))

(deftest should-build-properties-without-ssl
  (let [props (build-properties {:host "acme.com" :port "1234" :user "Wiley"})]
    (is (= "acme.com" (.get props "mail.smtp.host")))
    (is (= "1234" (.get props "mail.smtp.port")))
    (is (= "Wiley" (.get props "mail.smtp.user")))
    (is (= "1234" (.get props "mail.smtp.socketFactory.port")))
    (is (= "true" (.get props "mail.smtp.auth")))
    (is (= nil (.get props "mail.smtp.starttls.enable"))) ))

(deftest should-build-properties-with-ssl
  (let [props (build-properties {:host "acme.com" :port "1234" :user "Wiley" :ssl true})]
    (is (= "acme.com" (.get props "mail.smtp.host")))
    (is (= "1234" (.get props "mail.smtp.port")))
    (is (= "Wiley" (.get props "mail.smtp.user")))
    (is (= "1234" (.get props "mail.smtp.socketFactory.port")))
    (is (= "true" (.get props "mail.smtp.auth")))
    (is (= "true" (.get props "mail.smtp.starttls.enable")))
    (is (= "javax.net.ssl.SSLSocketFactory" (.get props "mail.smtp.socketFactory.class")))
    (is (= "false" (.get props "mail.smtp.socketFactory.fallback")))))

(deftest should-build-properties-with-ssl-using-string-true
  (let [props (build-properties {:host "acme.com" :port "1234" :user "Wiley" :ssl "true"})]
    (is (= "true" (.get props "mail.smtp.starttls.enable")))))

(deftest should-create-authenticator
  (let [authenticator (create-authenticator "Wiley" "Coyote")]
    (is (= "Wiley" (.getUserName (.getPasswordAuthentication authenticator))))
    (is (= "Coyote" (.getPassword (.getPasswordAuthentication authenticator))))))

(defn sample-session []
  (let [props (build-properties {:host "acme.com" :port "1234" :user "Wiley"})
        authenticator (create-authenticator "Wiley" "Coyote")
        session (javax.mail.Session/getDefaultInstance props authenticator)]
    session))

(deftest should-create-message
  (let [session (sample-session)
        message (create-message session {:to ["joe@acme.com"] :from "jill@acme.com" :subject "Hiya" :text ":)"})]
    (is (= "Hiya" (.getSubject message)))
    (is (= ":)" (.getContent message)))
    (is (= ["jill@acme.com"] (map #(.getAddress %1) (.getFrom message))))
    (is (= ["joe@acme.com"] (map #(.getAddress %1) (.getRecipients message javax.mail.Message$RecipientType/TO))))
    (is (= [] (map #(.getAddress %1) (.getRecipients message javax.mail.Message$RecipientType/CC))))
    (is (= [] (map #(.getAddress %1) (.getRecipients message javax.mail.Message$RecipientType/BCC))))))

(deftest should-create-message-with-string-to
  (let [session (sample-session)
        message (create-message session {:to "joe@acme.com" :from "jill@acme.com" :subject "Hiya" :text ":)"})]
    (is (= ["joe@acme.com"] (map #(.getAddress %1) (.getRecipients message javax.mail.Message$RecipientType/TO))))))

(deftest should-create-message-with-cc-and-bcc
  (let [session (sample-session)
        message (create-message session {:to ["joe@acme.com"] :cc "bill@acme.com" :bcc "bob@acme.com"
                                         :from "jill@acme.com" :subject "Hiya" :text ":)"})]
    (is (= "Hiya" (.getSubject message)))
    (is (= ":)" (.getContent message)))
    (is (= ["jill@acme.com"] (map #(.getAddress %1) (.getFrom message))))
    (is (= ["joe@acme.com"] (map #(.getAddress %1) (.getRecipients message javax.mail.Message$RecipientType/TO))))
    (is (= ["bill@acme.com"] (map #(.getAddress %1) (.getRecipients message javax.mail.Message$RecipientType/CC))))
    (is (= ["bob@acme.com"] (map #(.getAddress %1) (.getRecipients message javax.mail.Message$RecipientType/BCC))))))

(deftest should-create-message-multiple-recipients
  (let [session (sample-session)
        message (create-message session {:to ["joe@acme.com" "tom@acme.com"] :from "jill@acme.com" :subject "Hiya" :text ":)"})]
    (is (= ["joe@acme.com" "tom@acme.com"] (map #(.getAddress %1) (.getRecipients message javax.mail.Message$RecipientType/TO))))))



(clojure.contrib.test-is/run-tests)