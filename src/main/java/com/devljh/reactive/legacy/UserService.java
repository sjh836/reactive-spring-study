package com.devljh.reactive.legacy;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void signup(final User user) {
        // 회원가입 핵심 비즈니스 로직...

        // 부가적 비즈니스 수행 등을 위하여 이벤트 전파
        applicationEventPublisher.publishEvent(new SignupEvent(user));
    }
}
