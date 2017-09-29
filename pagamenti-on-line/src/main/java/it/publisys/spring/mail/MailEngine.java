package it.publisys.spring.mail;

import java.io.File;
import java.util.Date;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 *
 * @author mcolucci
 */
public class MailEngine {

    private static final Logger _log = LoggerFactory.getLogger(MailEngine.class);
    //
    private final JavaMailSender sender;
    //
    public static final String HTML_CONTENT_TYPE = "text/html";
    public static final String TXT_CONTENT_TYPE = "text/plain";

    public MailEngine(JavaMailSender sender) {
        this.sender = sender;
    }

    public void send(String to, String subject, String content) throws Exception {
        send(to, subject, content, null, HTML_CONTENT_TYPE);
    }

    public void send(String to, String subject, String content,
                     String[] attachments) throws Exception {
        send(to, subject, content, attachments, HTML_CONTENT_TYPE);
    }

    public void sendSMS(String to, String subject, String content) throws
        Exception {
        send(to, subject, content, null, TXT_CONTENT_TYPE);
    }

    private void send(String to, String subject, String content,
                      String[] attachments, String contentType) throws Exception {
        try {
            MimeMessage message = this.sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(to);
            helper.setSubject(subject);

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();
            // Fill the message
            messageBodyPart.setContent(content, contentType);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            // Part two is attachment
            if (attachments != null) {
                for (String att : attachments) {
                    File file = new File(att);
                    messageBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(file);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(file.getName());
                    multipart.addBodyPart(messageBodyPart);
                }
            }
            // Put parts in message
            message.setContent(multipart);
            // setto le informazioni per l'header
            message.setSentDate(new Date());

             this.sender.send(message);

            _log.info("E-Mail inviata all'indirizzo '" + to + "'.");

        } catch (Exception e) {
            _log.warn("Impossibile inviare la mail all'indirizzo '" + to + "'.", e);
            throw e;
        }
    }
}
