package com.devljh.reactive.practice;

import static org.junit.Assert.*;

import java.time.Duration;
import java.util.List;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class ReactorTest {
    @Test
    public void 무한시퀀스_테스트() {
        Flux.range(1, 5)
                //.repeat()
                .subscribe(number -> log.info("{}", number));
    }

    @Test
    public void 동작하지않는_무한시퀀스_테스트() {
        List<Integer> list = Flux.range(1, 5)
                //.repeat()
                .collectList()
                .block();

        log.info("{}", list);
    }

    @Test
    public void defer_테스트() throws Exception {
        long start = System.currentTimeMillis();

        // just
        Mono<Long> clock1 = Mono.just(System.currentTimeMillis());

        Thread.sleep(5000);
        long result1 = clock1.block() - start;
        log.info("흐른 시간 = {}", result1); // 5초가 지났으나, 즉발실행되어서 0 출력
        //assertTrue(result1 >= 5000);
        assertEquals( 0, result1);
        Mono.just(System.nanoTime()).repeat(3).subscribe(c -> log.info("{}", c));

        // fromSupplier
        Mono<Long> clock2 = Mono.fromSupplier(System::currentTimeMillis);

        Thread.sleep(5000);
        long result2 = clock2.block() - start;
        log.info("흐른 시간 = {}", result2); // 10초 지남
        assertTrue(result2 >= 5000);
        Mono.fromSupplier(System::nanoTime).repeat(3).subscribe(c -> log.info("{}", c));


        // defer
        Mono<Long> clock3 = Mono.defer(() -> Mono.just(System.currentTimeMillis()));

        Thread.sleep(5000);
        long result3 = clock3.block() - start;
        log.info("흐른 시간 = {}", result3); // 15초 지남
        assertTrue(result3 >= 15000);
        Mono.defer(() -> Mono.just(System.nanoTime())).repeat(3).subscribe(c -> log.info("{}", c));
    }

    @Test
    public void subscribe_테스트() {
        // onNext, onError, onComplete 처리
        Flux.just("A", "B", "C")
                .subscribe(
                        data -> log.info("onNext = {}", data),
                        err -> {},
                        () -> log.info("onComplete")
                );

        // subscription 직접 제어
        Flux.range(1, 100)
                .subscribe(
                        data -> log.info("onNext = {}", data),
                        err -> {},
                        () -> log.info("onComplete"),
                        subscription -> {
                            subscription.request(5);
                            subscription.cancel();
                        }
                );
    }

    @Test
    public void 구독취소_테스트() throws Exception {
        Disposable disposable = Flux.interval(Duration.ofMillis(500))
                .subscribe(i -> log.info("{}", i));
        Thread.sleep(2000);
        disposable.dispose();
    }

    @Test
    public void Subscriber_잘못된_구현() {
        Subscriber<String> subscriber = new Subscriber<String>() {
            /*
             * 저번 포스팅의 리액티브 스트림 파이프라인 병렬화에서 보았듯이,
             * 발행과 구독은 각각 다른 쓰레드에서 처리될 수 있다.
             * 따라서, volatile 키워드를 사용한다.
             */
            private volatile Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                this.subscription.request(1); // 구독 후, 최초 요청
            }

            @Override
            public void onNext(String s) {
                log.info("onNext = {}", s);
                this.subscription.request(1); // 데이터 수신 후, 추가 요청
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };

        Flux.just("A", "B", "C").subscribe(subscriber);
    }

    @Test
    public void Subscriber_안전한_구현() {
        Subscriber<String> subscriber = new BaseSubscriber<String>() {
            @Override
            public void hookOnSubscribe(Subscription subscription) {
                request(1); // 구독 후, 최초 요청
            }

            @Override
            public void hookOnNext(String s) {
                log.info("onNext = {}", s);
                request(1); // 데이터 수신 후, 추가 요청
            }

            @Override
            public void hookOnComplete() {
                log.info("onComplete");
            }
        };

        Flux.just("A", "B", "C").subscribeWith(subscriber);
    }
}
