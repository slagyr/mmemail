(ns mmemail.helper)

(defn build-properties [map]
  (let [props (java.util.Properties.)]
    (doto props
      (.put "mail.smtp.host" (:host map))
      (.put "mail.smtp.port" (:port map))
      (.put "mail.smtp.user" (:user map))
      (.put "mail.smtp.socketFactory.port" (:port map))
      (.put "mail.smtp.auth" "true"))
    (if (or (= (:ssl map) true) (= (:ssl map) "true"))
      (doto props
        (.put "mail.smtp.starttls.enable" "true")
        (.put "mail.smtp.socketFactory.class" "javax.net.ssl.SSLSocketFactory")
        (.put "mail.smtp.socketFactory.fallback" "false")))
    props))

(defn validate-session-params [params]
  (if (nil? (:host params)) (throw (Exception. "Missing :host")))
  (if (nil? (:port params)) (throw (Exception. "Missing :port")))
  (if (nil? (:user params)) (throw (Exception. "Missing :user"))))

(defn create-authenticator [username password]
  (proxy [javax.mail.Authenticator] []
    (getPasswordAuthentication []
      (javax.mail.PasswordAuthentication. username password))))

(defn create-session [params]
  (validate-session-params params)
  (let [props (build-properties params)
        authenticator (create-authenticator (:user params) (:password params))]
    (javax.mail.Session/getDefaultInstance props authenticator)))

(defn add-recipients [message recipient-type recipients]
  (if (not (nil? recipients))
    (if (sequential? recipients)
      (doseq [recipient recipients]
        (.addRecipients message recipient-type (javax.mail.internet.InternetAddress/parse recipient)))
      (add-recipients message recipient-type (list recipients)))))

(defn create-message [session details]
  (let [message (javax.mail.internet.MimeMessage. session)]
    (.setFrom message (javax.mail.internet.InternetAddress. (:from details)))
    (add-recipients message (javax.mail.Message$RecipientType/TO) (:to details))
    (add-recipients message (javax.mail.Message$RecipientType/CC) (:cc details))
    (add-recipients message (javax.mail.Message$RecipientType/BCC) (:bcc details))
    (.setSubject message (:subject details))
    (.setText message (:text details))
    message))

(defn deliver-message [message]
  (javax.mail.Transport/send message))