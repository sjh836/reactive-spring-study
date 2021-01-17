package com.devljh.reactive.legacy;

import java.util.Random;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SignupScheduler {
    private final UserService userService;

    private Random random = new Random();

    //@Scheduled(fixedDelay = 2000)
    public void signupTask() {
        userService.signup(createTestUser());
    }

    private User createTestUser() {
        return new User("빨간색소년", random.nextInt(30));
    }
}
