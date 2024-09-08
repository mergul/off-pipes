package com.streams.pipes.config.processor;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

import static java.time.LocalTime.now;

public class ReactorEventBusImpl<T> {
    private final Sinks.EmitFailureHandler myEmitFailureHandler = (signalType, emitResult) -> emitResult
            .equals(Sinks.EmitResult.FAIL_NON_SERIALIZED) ? true : false;
    public static final RetryBackoffSpec RETRY_SPEC =
            Retry.backoff(5, Duration.ofSeconds(2))
                    .doBeforeRetry(retrySignal -> System.out.printf("[%s][%s] Error, before retry\n", now(), Thread.currentThread().getName()))
                    .doAfterRetry(retrySignal -> System.out.printf("[%s][%s] Error, after retry\n", now(), Thread.currentThread().getName()))
                    .scheduler(Schedulers.boundedElastic())
                    .transientErrors(false);
    private final Sinks.Many<T> sink = Sinks.many().replay().limit(2);
    private final Flux<T> eventFlux = sink.asFlux()
        .publishOn(Schedulers.newParallel("bus"));
    public void publish(T event) {
        sink.emitNext(event, this.myEmitFailureHandler);
    }
    public Flux<T> receive() {
        return eventFlux;
    }
    private static Disposable subscribeToBus(ReactorEventBusImpl<Integer> bus, Integer subscribtionNumber) {
        return bus.receive()
                .retryWhen(RETRY_SPEC)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(i -> System.out.printf("[%s][%s] got %s\n", subscribtionNumber, Thread.currentThread().getName(), i))
                .subscribe();
    }
}
