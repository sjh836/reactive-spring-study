package com.devljh.reactive.rxjava;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import rx.Subscriber;

public class RxSseEmitter<T> extends SseEmitter {
    private Subscriber<T> subscriber;

    public RxSseEmitter(long timeout) {
        super(timeout);

        this.subscriber = new Subscriber<T>() {
            @Override
            public void onNext(T user) {
                try {
                    RxSseEmitter.this.send(user);
                } catch (Exception e) {
                    unsubscribe();
                }
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };

        onCompletion(subscriber::unsubscribe);
        onTimeout(subscriber::unsubscribe);
    }

    public Subscriber<T> getSubscriber() {
        return subscriber;
    }
}
