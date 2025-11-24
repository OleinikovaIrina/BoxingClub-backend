package de.oleinikova.boxingclub.backend.mail.test;

import de.oleinikova.boxingclub.backend.mail.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/test-mail")
@RequiredArgsConstructor
public class TestMailController {
    private final EmailService emailService;

    @PostMapping("/text")
    public ResponseEntity<String> sendText(
            @RequestParam String to,
            @RequestParam(defaultValue = "BoxingClub Test") String subject,
            @RequestParam(defaultValue = "Hello from BoxingClub!") String body

    ) {
        emailService.sendSimpleMail(to, subject, body);
        return ResponseEntity.ok("Text mail sent to " + to);
    }

    @PostMapping("/html")
    public ResponseEntity<String> sendHtml(
            @RequestParam String to,
            @RequestParam(defaultValue = "Welcome") String subject
    ) throws MessagingException {
        String html = """
                <h2>Hallo!</h2>
                <p>Dies ist ein <b>HTML</b> Testmail.</p>
                <p><a href="https://example.com" target="_blank">Öffnen</a></p>
                """;

        emailService.sendHtmlMail(to, subject, html);
        return ResponseEntity.ok("Html mail sent to " + to);
    }

}
