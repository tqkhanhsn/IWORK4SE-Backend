package vn.iwork4se.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-SERVICE")
public class EmailService {

    private final SendGrid sendGrid;

    @Value("${spring.sendGrid.fromEmail}")
    private String from;
    @Value ("${spring.sendGrid.templateId}")
    private String template;
    @Value ("${spring.sendGrid.verificationLink}")
    private String verificationLink;


    public void emailVerification(String to, String name) throws IOException {
        log.info("Sending email to: {}, name: {}", to, name);

        Email fromEmail = new Email(from, "iWork4SE");
        Email toEmail = new Email(to);

        String secretCode = UUID.randomUUID().toString();
        log.info("secretCode = {}", secretCode);


        Map<String, String> dynamicTemplateData  = new HashMap<>();
        dynamicTemplateData .put("name", name);
        dynamicTemplateData .put("verification_link", verificationLink + "?secretCode=" + secretCode);

        Mail mail = new Mail();
        mail.setFrom(fromEmail);

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);


        dynamicTemplateData.forEach(personalization::addDynamicTemplateData);
        mail.addPersonalization(personalization);

        mail.setTemplateId(template);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");


        request.setBody(mail.build());
        Response response = sendGrid.api(request);

        if (response.getStatusCode() == 202) {
            log.info("Email sent successfully");
        } else {
            log.error("Email sent failed - statusCode={}, body={}", response.getStatusCode(), response.getBody());
        }
    }
}
