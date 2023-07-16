package com.streams.pipes.chat;

import com.google.common.collect.Lists;
import com.streams.pipes.model.*;
import lombok.Synchronized;
import org.apache.kafka.common.serialization.Serdes;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.lang.NonNull;
import reactor.core.Disposable;
import reactor.core.publisher.*;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

import static java.time.LocalTime.now;

public class RoomEntre<T> implements ChatRoomMessageListener<T> {
   // private static final Logger logger = LoggerFactory.getLogger(RoomEntre.class);
    private final Flux<ServerSentEvent<T>> hotFlux;
    private final Sinks.Many<ServerSentEvent<T>> sink;
    private Date lastNewsEmit;
    private Date lastOffersEmit;
    public static final RetryBackoffSpec RETRY_SPEC =
            Retry.backoff(5, Duration.ofSeconds(2))
                    .doBeforeRetry(retrySignal -> System.out.printf("[%s][%s] Error, before retry\n", now(), Thread.currentThread().getName()))
                    .doAfterRetry(retrySignal -> System.out.printf("[%s][%s] Error, after retry\n", now(), Thread.currentThread().getName()))
                    .scheduler(Schedulers.boundedElastic())
                    .transientErrors(false);
    private Date lastTagsEmit;
    private Disposable myDisposable;
    private Disposable disposable;
    private Disposable disposableNews;
    private Disposable disposableTags;
    private Disposable disposableCounts;
    private Disposable disposableOffers;
    private ServerSentEvent<T> lastRecord;
    private final Map<String, List<String>> newsIds;
    private final Map<String, List<String>> offersIds;
    private final Sinks.EmitFailureHandler myEmitFailureHandler = (signalType, emitResult) -> emitResult
            .equals(Sinks.EmitResult.FAIL_NON_SERIALIZED) ? true : false;
   // private final Map<String, Disposable> subscriberMap;//BaseSubscriber<ServerSentEvent<T>>

    public RoomEntre(@Qualifier("hotFlux") Flux<ServerSentEvent<T>> hotFlux,
                     @Qualifier("sink") Sinks.Many<ServerSentEvent<T>> sink) {
        this.hotFlux = hotFlux;
        this.sink = sink;
        this.newsIds = new HashMap<>();
        this.offersIds = new HashMap<>();
       // this.subscriberMap = new HashMap<>();
        this.disposable = Mono.just("h").delayElement(Duration.ofSeconds(55)).subscribe(s1 -> {
            if (this.lastRecord!=null) onPostMessage(this.lastRecord.data(), this.lastRecord.id(), null, this.lastRecord.event());
        });
        this.myDisposable = null;
        // this.hotFlux.subscribe(this.processor::onNext);
    }

    private void emitHeartBeat(String s) {
        this.disposable.dispose();
        this.disposable = Mono.just("h").delayElement(Duration.ofSeconds(55))
                .subscribe(s1 -> onPostMessage(this.lastRecord.data(), this.lastRecord.id(), null, this.lastRecord.event()));
    }

    @Override
    public void onPostMessage(T msg, String key, Date date, String ev) {
        if (msg instanceof TopThreeHundredNews) {
              //  logger.info("emit  {} -- {} -- {}", ev, key, date);
                TopThreeHundredNews threeHundredNews = new TopThreeHundredNews();
                threeHundredNews.getList().addAll(getTopList(((TopThreeHundredNews)msg).getList()));
                this.disposableNews= Mono.fromCallable(() -> publish(getMyEvent((T) threeHundredNews, key, ev)))
                        .onErrorResume(e -> Mono.empty())
                        .subscribeOn(Schedulers.boundedElastic())
                        .thenMany(getPartialEvents(getSkipList(((TopThreeHundredNews) msg).getList()), key, ev))
                        .delayElements(Duration.ofSeconds(1L))
                        .flatMapSequential(tEvent -> Mono.fromCallable(() -> publish(tEvent)).onErrorResume(e -> Mono.empty()))
                        .subscribeOn(Schedulers.parallel())
                        .subscribe();
                this.lastNewsEmit = date;
        } else if (msg instanceof TopThreeHundredOffers) {
              //  logger.info("emit offers {} -- {} -- {}", ev, key, date);
                TopThreeHundredOffers threeHundredOffers = new TopThreeHundredOffers();
                threeHundredOffers.getList().addAll(getTopOfferList(((TopThreeHundredOffers)msg).getList()));
                this.disposableOffers= Mono.fromCallable(() -> publish(getMyEvent((T) threeHundredOffers, key, ev)))
                        .onErrorResume(e -> Mono.empty())
                        .subscribeOn(Schedulers.boundedElastic())
                        .thenMany(getPartialOfferEvents(getSkipOfferList(((TopThreeHundredOffers) msg).getList()), key, ev))
                        .delayElements(Duration.ofSeconds(1L))
                        .flatMapSequential(tEvent -> Mono.fromCallable(() -> publish(tEvent))
                                .onErrorResume(e -> Mono.empty()))
                        .subscribeOn(Schedulers.parallel()).subscribe();
                this.lastOffersEmit = date;
        } else if (msg instanceof TopHundredNews &&(this.lastTagsEmit == null || date == null
                || ((date.getTime() - this.lastTagsEmit.getTime()) / (1000) % 60) > 30)) {
          //  logger.info("emit  {} -- {} -- {}", ev, key, date);
            this.disposableTags= Mono.fromCallable(() -> publish(getMyEvent(msg, key, ev)))
                    .onErrorResume(e -> Mono.empty())
                    .subscribeOn(Schedulers.boundedElastic()).subscribe();
            this.lastTagsEmit = date;
        } else {
          //  logger.info("finished BalanceRecord key {}, event {}, total balance --> {}", key, ev, msg);
            if (msg!=null) {
                this.disposableCounts = Mono.fromCallable(() -> publish(getMyEvent(msg, key, ev)))
                        .onErrorResume((e) -> Mono.empty())
                        .subscribeOn(Schedulers.boundedElastic()).subscribe();
            }
        }
        this.lastRecord=getMyEvent(msg, key, ev);
        emitHeartBeat(key);
    }
    public Mono<Void> publish(ServerSentEvent<T> event) {
        sink.emitNext(event, this.myEmitFailureHandler);
        return Mono.empty();
    }
    public Flux<ServerSentEvent<T>> subscribe(String lastEventId) {
//        BaseSubscriber<ServerSentEvent<T>> busses = new BaseSubscriber<ServerSentEvent<T>>() {
//            @Override
//            public void hookOnSubscribe(Subscription subscription) {
//                logger.info("BaseSubscriber hookOnSubscribe {}", subscription);
//                //request(1);
//            }
//            @Override
//            public void hookOnNext(ServerSentEvent<T> value) {
//                request(1);
//            }
//            @Override
//            public void hookOnError(Throwable throwable){
//                System.err.println(throwable.getStackTrace());
//            }
//            @Override
//            protected void hookFinally(@NonNull SignalType type) {
//                super.hookFinally(type);
//                System.out.println(type);
//            }
//        };
       // logger.info("this sink is : {}", this.sink);
        this.start();
        return this.hotFlux;
    }

    public Map<String, List<String>> getNewsIds() {
        return newsIds;
    }
    public Map<String, List<String>> getOffersIds() {
        return offersIds;
    }

    public ServerSentEvent<T> getMyEvent(T msg, String key, String ev) {
        return ServerSentEvent.<T>builder().event(ev).data(msg).id(key).comment("keep alive").build();
    }

    public Collection<NewsPayload> getTopList(Collection<NewsPayload> list) {
        return list.stream().limit(10).collect(Collectors.toList());
    }

    public Collection<OfferPayload> getTopOfferList(Collection<OfferPayload> list) {
        return list.stream().limit(10).collect(Collectors.toList());
    }
    public List<NewsPayload> getSkipList(Collection<NewsPayload> list) {
        return list.stream().skip(10).collect(Collectors.toList());
    }
    public List<OfferPayload> getSkipOfferList(Collection<OfferPayload> list) {
        return list.stream().skip(10).collect(Collectors.toList());
    }
    public Flux<ServerSentEvent<T>> getPartialEvents(List<NewsPayload> msgList, String key, String ev) {
        Iterable<List<NewsPayload>> lists = Lists.partition(msgList, 10);
        return Flux.fromIterable(lists).publishOn(Schedulers.boundedElastic()).flatMap(list -> {
            TopThreeHundredNews titan = new TopThreeHundredNews();
            titan.getList().addAll(list);
         //   logger.info("finished bounded {}, -- {}, -- {}", key, ev, list.size());
            return Mono.fromCallable(() -> getMyEvent((T) titan, key, ev));
        });
    }
    public Flux<ServerSentEvent<T>> getPartialOfferEvents(List<OfferPayload> msgList, String key, String ev) {
        Iterable<List<OfferPayload>> lists = Lists.partition(msgList, 10);
        return Flux.fromIterable(lists).publishOn(Schedulers.boundedElastic()).flatMap(list -> {
            TopThreeHundredOffers titan = new TopThreeHundredOffers();
            titan.getList().addAll(list);
            //   logger.info("finished bounded {}, -- {}, -- {}", key, ev, list.size());
            return Mono.fromCallable(() -> getMyEvent((T) titan, key, ev));
        });
    }
    public Mono<Boolean> unsubscribe(String chatRoom) {
       // if (!this.subscriberMap.isEmpty()) {
            //BaseSubscriber<ServerSentEvent<T>> busses = this.subscriberMap.get(chatRoom);
       //     this.myDisposable = this.subscriberMap.get(chatRoom);
            if (this.myDisposable!=null) {
                stop();//busses.dispose();// busses.cancel();
                //this.subscriberMap.remove(chatRoom);
                this.disposable.dispose();
                if(this.disposableNews!=null) this.disposableNews.dispose();
                if(this.disposableTags!=null) this.disposableTags.dispose();
                if(this.disposableCounts!=null) this.disposableCounts.dispose();
                if(this.disposableOffers!=null) this.disposableOffers.dispose();
               // logger.info("BaseSubscriber to cancel chatRoom {}, subscriberMap size {}", chatRoom, subscriberMap.size());
            }
       // }
        return Mono.just(true);
    }
    public static Sinks.EmitFailureHandler retryOnNonSerializedElse(Sinks.EmitFailureHandler fallback){
        return (signalType, emitResult) -> {
            if (emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED) {
                LockSupport.parkNanos(10);
                return true;
            } else
                return fallback.onEmitFailure(signalType, emitResult);
        };
    }
    @Synchronized
    public void start() {
        if (!isRunning()) {
            this.myDisposable = hotFlux.retryWhen(RETRY_SPEC).subscribeOn(Schedulers.boundedElastic()).subscribe();
        }
    }

    @Synchronized
    public void stop() {
        this.myDisposable.dispose();
        this.myDisposable = null;
    }

    @Synchronized
    public Boolean isRunning() {
        return this.myDisposable != null;
    }
}
