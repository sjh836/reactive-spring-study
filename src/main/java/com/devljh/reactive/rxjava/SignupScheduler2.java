package com.devljh.reactive.rxjava;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import rx.Observable;

import com.devljh.reactive.legacy.User;

@Component
@RequiredArgsConstructor
public class SignupScheduler2 {
    private final UserService2 userService2;

    private Observable<User> hotStream;
    private Random random = new Random();

    @PostConstruct
    private void signupTask() {
        this.hotStream = Observable.interval(2000, TimeUnit.MILLISECONDS)
                .map(tick -> createTestUser(tick))
                .share(); // publish + refCount
        signupUserStream().subscribe(userService2::signup);
    }

    public Observable<User> signupUserStream() {
        return this.hotStream;
    }

    private User createTestUser(long tick) {
        System.out.println(tick);
        return new User("빨간색소년", random.nextInt(30));
    }
}
