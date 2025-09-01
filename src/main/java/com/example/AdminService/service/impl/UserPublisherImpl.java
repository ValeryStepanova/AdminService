package com.example.AdminService.service.impl;

import com.example.AdminService.config.RabbitConfig;
import com.example.AdminService.dto.event.UserUpdateEvent;
import com.example.AdminService.service.UserPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPublisherImpl implements UserPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishRoleUpdate(UserUpdateEvent userUpdateEvent) {
        log.info("UserPublisher impl activated");
        rabbitTemplate.convertAndSend(
                RabbitConfig.USER_ROLE_UPDATED_EXCHANGE,
                "user.role.updated",
                userUpdateEvent
        );
        log.info("UserPublisher impl end");
    }
}
