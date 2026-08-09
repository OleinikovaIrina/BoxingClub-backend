package de.oleinikova.boxingclub.backend.mail;


import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    public void sendSimpleMail(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);

        mailSender.send(msg);
    }

    //MIME (Multipurpose Internet Mail Extensions),
    public void sendHtmlMail(String to, String subject, String htmlBody) throws MessagingException {

        MimeMessage mime = mailSender.createMimeMessage();

        mime.setHeader("X-Mailin-Track-Click", "0");
        mime.setHeader("X-Mailin-Track-Open", "0");

        MimeMessageHelper helper = new MimeMessageHelper(mime, false,"UTF-8");

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(mime);
    }

}
