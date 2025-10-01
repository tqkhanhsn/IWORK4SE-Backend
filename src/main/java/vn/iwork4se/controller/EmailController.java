package vn.iwork4se.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.iwork4se.service.EmailService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
@Tag(name="Email controller")
@Slf4j(topic = "EMAIL_CONTROLLER")
public class EmailController {
    private final EmailService emailService;

    @GetMapping("/verify-email")
    public void emailVerification(@RequestHeader("Authorization") String authHeader,@RequestParam String to, @RequestParam String name){
        log.info("Sending email to: {}", to);
        String token = authHeader.substring(7).trim();
        try {
            emailService.emailVerification(token,to,name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Email sent to: {}", to);
    }
}
