package com.devljh.reactive.practice;

import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

@Slf4j
public class ReactiveStreamsTest {
    @Test
    public void 스트림의_생산과_소비_기초() {
        // 스트림 생산 : 구독자가 구독하는 즉시 구독자에게 이벤트를 전파하는 일종의 이벤트 생성기
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> sub) {
                sub.onNext("Hello, reactive world!");
                sub.onNext("저는 빨간색소년입니다.");
                sub.onCompleted();
            }
        });

        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                log.info(s);
            }

            @Override
            public void onCompleted() {
                log.info("수행 완료!");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        };

        observable.subscribe(subscriber);

        // forEach 는 subscriber 를 내가 만들진 않았지만, 내부적으로 만들어서 사용한다.
        observable.forEach(log::info);
    }

    @Test
    public void 비동기_시퀀스() throws Exception {
        Subscription subscription = Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(e -> log.info("수신 데이터 = {}", e));
        Thread.sleep(5000);
        subscription.unsubscribe();
        Thread.sleep(5000);
    }

    @Test
    public void map연산자_구현() {
        // 기대
        Observable.range(0, 5)
                .map(i -> i * 2)
                .forEach(i -> log.info("결과 = {}", i));

        // 구현
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                // OnSubscribeRange 에서도 반복문에서 onNext(index); 하다가 if (index == endIndex) 면 onCompleted 하고 끝낸다.
                for (int i = 0; i < 5; i++) {
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        });

        Function<Integer, Integer> transformer = i -> i * 2;

        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            @Override
            public void onNext(Integer i) {
                i = transformer.apply(i); // OnSubscribeMap 에서도 subscriber.onNext(transformer.apply(t))
                log.info("결과 = {}", i);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };

        observable.subscribe(subscriber);

        // 람다 사용
        Observable.create(sub -> {
            for (int i = 0; i < 5; i++) {
                sub.onNext(i);
            }
            sub.onCompleted();
        }).subscribe(i -> {
            i = transformer.apply((Integer) i); // 타입추론을 할 수 없어서 형 변환
            log.info("결과 = {}", i);
        });
    }

    @Test
    public void filter연산자_구현() {
        // 기대
        Observable.range(0, 5)
                .filter(i -> i % 2 == 0)
                .forEach(i -> log.info("결과 = {}", i));

        // 구현
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        });

        Predicate<Integer> predicate = i -> i % 2 == 0;

        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            @Override
            public void onNext(Integer i) {
                if (predicate.test(i)) {
                    log.info("결과 = {}", i);
                }
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };

        observable.subscribe(subscriber);

        // 람다 사용
        Observable.create(sub -> {
            for (int i = 0; i < 5; i++) {
                sub.onNext(i);
            }
            sub.onCompleted();
        }).subscribe(i -> {
            if (predicate.test((Integer) i)) {
                log.info("결과 = {}", i);
            }
        });
    }

    @Test
    public void reduce연산자_구현() {
        // 기대
        Observable.range(0, 5)
                .reduce((x, y) -> x + y)
                .forEach(i -> log.info("결과 = {}", i));

        // 구현
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        });

        BiFunction<Integer, Integer, Integer> accumulator = (x, y) -> x + y;

        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            private Integer result = null;

            @Override
            public void onNext(Integer i) {
                Integer temp = result;
                if (temp == null) {
                    result = i;
                } else {
                    result = accumulator.apply(temp, i);
                }
            }

            @Override
            public void onCompleted() {
                log.info("결과 = {}", this.result);
            }

            @Override
            public void onError(Throwable e) {
            }
        };

        observable.subscribe(subscriber);
    }

    @Test
    public void zip연산자_구현() {
        // 기대
        Observable.zip(
                Observable.just("A", "B", "C", "D"),
                Observable.just(1, 2, 3),
                (x, y) -> x + y
        ).forEach(i -> log.info("결과 = {}", i));

        // 구현
        Observable<String> observable1 = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("A");
                subscriber.onNext("B");
                subscriber.onNext("C");
                subscriber.onNext("D");
                subscriber.onCompleted();
            }
        });

        Observable<Integer> observable2 = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(1);
                subscriber.onNext(2);
                subscriber.onNext(3);
                subscriber.onCompleted();
            }
        });
    }
}
