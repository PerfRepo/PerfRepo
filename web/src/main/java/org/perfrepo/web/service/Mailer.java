package org.perfrepo.web.service;

import javax.annotation.Resource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Utility class that encapsulates usage of mailing mechanism.
 *
 * @author Jakub Markos <jmarkos@redhat.com>
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class Mailer {

   @Resource(mappedName = "java:jboss/mail/perfreposmtp")
   private Session emailSession;

   /**
    * Sends a mail.
    *
    * @param recipient email address of the recipient
    * @param subject
    * @param body      text of the email
    * @throws javax.mail.MessagingException
    */
   public void sendEmail(String recipient, String subject, String body) throws MessagingException {
      Message message = new MimeMessage(emailSession);
      Address toAddress = new InternetAddress(recipient);
      message.addRecipient(Message.RecipientType.TO, toAddress);
      message.setSubject(subject);
      message.setContent(body, "text/html");
      Transport.send(message);
   }
}
