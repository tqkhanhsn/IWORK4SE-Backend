package vn.iwork4se.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.iwork4se.service.EmailService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Tag(name="Email controller")
@Slf4j(topic = "EMAIL_CONTROLLER")
public class EmailController {
    private final EmailService emailService;

    @GetMapping("/verify-email")
    public void emailVerification(@RequestParam String to,@RequestParam String name){
        log.info("Sending email to: {}", to);
        try {
            emailService.emailVerification(to,name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Email sent to: {}", to);
    }
}
