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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-SERVICE")
public class EmailService {

    private final SendGrid sendGrid;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.sendGrid.fromEmail}")
    private String from;
    @Value ("${spring.sendGrid.templateId}")
    private String template;
    @Value ("${spring.sendGrid.resetPwdTemplateId}")
    private String resetPwdTemplate;
    @Value ("${spring.sendGrid.verificationLink}")
    private String verificationLink;


    public void emailVerification(String token, String to, String name) throws IOException {
        log.info("Sending email to: {}, name: {}", to, name);

        Email fromEmail = new Email(from, "iWork4SE");
        Email toEmail = new Email(to);

        String secretCode = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
        log.info("Generated secret code from encoded access token");

        String redisKey = "email_verification:" + to;
        redisTemplate.opsForValue().set(redisKey, secretCode, 1, TimeUnit.DAYS);
        log.info("Stored verification data in Redis with key: {} for 1 day", redisKey);


        Map<String, String> dynamicTemplateData  = new HashMap<>();
        dynamicTemplateData .put("name", name);
        dynamicTemplateData .put("verification_link", verificationLink + "?email=" + to+ "&secretCode=" + secretCode);

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


    public void sendForgotPasswordEmail(String email, String userName, String resetLink) throws IOException {
        log.info("Sending forgot password email to: {}", email);

        Email fromEmail = new Email(from, "iWork4SE");
        Email toEmail = new Email(email);

        Map<String, String> dynamicTemplateData = new HashMap<>();
        dynamicTemplateData.put("user_name", userName);
        dynamicTemplateData.put("reset_link", resetLink);
        dynamicTemplateData.put("expiration_time", "24 giờ");

        Mail mail = new Mail();
        mail.setFrom(fromEmail);

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);

        dynamicTemplateData.forEach(personalization::addDynamicTemplateData);
        mail.addPersonalization(personalization);

        mail.setTemplateId(resetPwdTemplate);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);

        if (response.getStatusCode() == 202) {
            log.info("Forgot password email sent successfully to: {}", email);
        } else {
            log.error("Forgot password email send failed - statusCode={}, body={}", response.getStatusCode(), response.getBody());
        }
    }
}
