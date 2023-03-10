package com.shevelyanchik.fitnessclub.emailservice.service.impl;

import com.shevelyanchik.fitnessclub.emailservice.dto.EmailDetails;
import com.shevelyanchik.fitnessclub.emailservice.service.EmailConsumerService;
import com.shevelyanchik.fitnessclub.emailservice.service.EmailService;
import com.shevelyanchik.fitnessclub.kafkaconfig.dto.EmailEvent;
import com.shevelyanchik.fitnessclub.kafkaconfig.topic.TopicName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailConsumerServiceImpl implements EmailConsumerService {

    private final EmailService emailService;
    @Value("${spring.mail.recipient}")
    private String recipient;

    @KafkaListener(
            topics = TopicName.EMAIL_TOPIC
    )
    @Override
    public void consume(EmailEvent event) {
        EmailDetails emailDetails = buildEmailDetails(event, recipient);
        emailService.sendEmail(emailDetails);
        log.info(event.toString());
    }

    private EmailDetails buildEmailDetails(EmailEvent event, String recipient) {
        return EmailDetails.builder()
                .recipient(recipient)
                .subject(event.getSubject())
                .message(event.getMessage())
                .build();
    }
}
