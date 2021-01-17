package com.devljh.reactive.rxjava;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.devljh.reactive.legacy.User;

@Slf4j
@Service
public class UserService2 {
    public void signup(final User user) {
        log.info("회원가입을 시도합니다. user = {}", user);

        // 회원가입 핵심 비즈니스 로직...
    }
}
