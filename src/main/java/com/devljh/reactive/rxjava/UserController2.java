package com.devljh.reactive.rxjava;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.devljh.reactive.legacy.User;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController2 {
    private static final long SSE_SESSION_TIMEOUT = 30 * 60 * 1000L;

    private final SignupScheduler2 signupScheduler2;

    @GetMapping(value = "/notice2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public RxSseEmitter signup(HttpServletRequest request) {
        log.info("SSE stream 접근 : {}", request.getRemoteAddr());

        RxSseEmitter<User> emitter = new RxSseEmitter<>(SSE_SESSION_TIMEOUT);

        signupScheduler2.signupUserStream().subscribe(emitter.getSubscriber());

        return emitter;
    }
}