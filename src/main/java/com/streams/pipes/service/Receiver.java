package com.streams.pipes.service;

import com.google.common.util.concurrent.*;
import com.streams.pipes.chat.RoomEntre;
import com.streams.pipes.config.streams.KStreamConf;
import com.streams.pipes.model.*;
import lombok.SneakyThrows;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

@Service
public class Receiver {
  // private static final Logger logger = LoggerFactory.getLogger(Sender.class);

    private static final String TOP_NEWS_STORE = "windowed-news-stores";
    private static final String USER_STORE = "stream-users-stores";
    private static final String MY_USER_STORE = "stream-musers-stores";
    private static final String TOP_OFFERS_STORE = "windowed-offers-stores";
    private final Sender kafkaSender;
    private final StreamsBuilderFactoryBean factoryBean;
    private final RoomEntre<?> entre;
    private final CountDownLatch latch = new CountDownLatch(1);
    final ListeningExecutorService pool = MoreExecutors.listeningDecorator(
            Executors.newFixedThreadPool(8)
    );

    public Receiver(Sender kafkaSender/*, @Qualifier(value = "miFlux") Flux<BalanceRecord> outputEvents*/, StreamsBuilderFactoryBean factoryBean, @Qualifier(value = "roomEntre") RoomEntre<?> entre) {
        this.kafkaSender = kafkaSender;
        this.factoryBean = factoryBean;
        this.entre = entre;
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @KafkaListener(topics = "${kafka.topics.auths}", properties = {"spring.json.value.default.type=com.streams.pipes.model.UserPayload", "spring.json.use.type.headers=false"})
    public void receiveFoo(UserPayload userPayload) {
      //  logger.info("received userPayload id = '{}' tags = '{}' users = '{}'", userPayload.getId(), userPayload.getTags(), userPayload.getUsers());
        String meId = "@" + userPayload.getId();
        RoomEntre<TopThreeHundredNews> chatRoomEntry = (RoomEntre<TopThreeHundredNews>) this.entre;
        final ListenableFuture<ReadOnlyKeyValueStore<String, TopThreeHundredNews>> topNewsFuture = future(TOP_NEWS_STORE);
        Mono<ReadOnlyKeyValueStore<String, TopThreeHundredNews>> topNewsStores = Mono.fromFuture(toCompletableFuture(topNewsFuture));

        if (userPayload.getIndex().equals(0)) {
           // List<Mono<Boolean>> allMono = new ArrayList<>();
            TopThreeHundredNews titan = new TopThreeHundredNews();
            Mono<Boolean> myTags = topNewsStores.flatMapMany(store -> {
                TopThreeHundredNews thn = store.get(meId);
                chatRoomEntry.getNewsIds().put(meId, thn == null ? Collections.emptyList() : thn.getList().stream().map(newsPayload -> newsPayload.getId().toHexString()).collect(Collectors.toList()));
                chatRoomEntry.onPostMessage(thn, "me", null, "top-news-" + meId + '-' + userPayload.getRandom());
                return Flux.fromIterable(userPayload.getTags())
                        .publishOn(Schedulers.boundedElastic()).map(sid -> {
                            TopThreeHundredNews ttt = store.get('#' + sid);
                            if (ttt != null) {
                                titan.getList().addAll(ttt.getList());
                                chatRoomEntry.getNewsIds().put('#' + sid, ttt.getList().stream().map(newsPayload -> newsPayload.getId().toHexString()).collect(Collectors.toList()));
                            }
                           // logger.info("MY tags list :'{}'", titan.getList());
                            return true;
                        });
            }).collectList().map(booleans -> {
                chatRoomEntry.onPostMessage(titan, "tags", null, "top-news-tags-" + meId + '-' + userPayload.getRandom());
                return true;
            });
            // allMono.add(myTags);
            TopThreeHundredNews python = new TopThreeHundredNews();
            Mono<Boolean> myUsers = topNewsStores.flatMapMany(store -> Flux.fromIterable(userPayload.getUsers())
                    .publishOn(Schedulers.boundedElastic()).map(sid -> {
                        TopThreeHundredNews ttt = store.get('@' + sid);
                        if (ttt != null) {
                            python.getList().addAll(ttt.getList());
                            chatRoomEntry.getNewsIds().put('@' + sid, ttt.getList().stream().map(newsPayload -> newsPayload.getId().toHexString()).collect(Collectors.toList()));
                        }
                    //    logger.info("MY users list :'{}'", python.getList());
                        return true;
                    })).collectList().map(bulls -> {
                chatRoomEntry.onPostMessage(python, "people", null, "top-news-people-" + meId + '-' + userPayload.getRandom());
                return true;
            });
            // allMono.add(myUsers);
            RoomEntre<RecordSSE> chatRoomEntry1 = (RoomEntre<RecordSSE>) this.entre;
            final ListenableFuture<ReadOnlyKeyValueStore<byte[], Long>> usersFuture = future(USER_STORE);
            Mono<ReadOnlyKeyValueStore<byte[], Long>> usersStores = Mono.fromFuture(toCompletableFuture(usersFuture));
            Mono<Boolean> myCounts = usersStores.map(store -> {
                chatRoomEntry1.onPostMessage(new RecordSSE(meId, store.get(meId.getBytes())), meId, null, "user-counts-" + meId);
              //  logger.info("MY record-sse list :'{}'", meId);
                return true;
            });
            // allMono.add(myCounts);

            TopThreeHundredOffers titanic = new TopThreeHundredOffers();
            RoomEntre<TopThreeHundredOffers> chatRoomEntry0 = (RoomEntre<TopThreeHundredOffers>) this.entre;
            final ListenableFuture<ReadOnlyKeyValueStore<String, TopThreeHundredOffers>> topOffersFuture = future(TOP_OFFERS_STORE);
            Mono<ReadOnlyKeyValueStore<String, TopThreeHundredOffers>> topOffersStores = Mono.fromFuture(Receiver.toCompletableFuture(topOffersFuture));
            Mono<Boolean> myOffers = topOffersStores.flatMapMany(store -> {
                TopThreeHundredOffers thn = store.get(meId);
                chatRoomEntry0.getOffersIds().put(meId, thn == null ? Collections.emptyList() : thn.getList().stream().map(offerPayload -> offerPayload.getId().toHexString()).collect(Collectors.toList()));
                chatRoomEntry0.onPostMessage(thn, "me", null, "top-offers-" + meId + '-' + userPayload.getRandom());
                TopThreeHundredOffers tln = store.get('@'+meId);
                chatRoomEntry0.getOffersIds().put('@'+meId, tln == null ? Collections.emptyList() : tln.getList().stream().map(offerPayload -> offerPayload.getId().toHexString()).collect(Collectors.toList()));
                chatRoomEntry0.onPostMessage(tln, "my", null, "top-offers-" +'@'+ meId + '-' + userPayload.getRandom());
              //  logger.info("MY offers offerlist :'{}'", tln.getList());
                return Flux.fromIterable(userPayload.getTags())
                        .publishOn(Schedulers.boundedElastic()).map(sid -> {
                            TopThreeHundredOffers ttt = store.get('#' + sid);
                            if (ttt != null) {
                                titanic.getList().addAll(ttt.getList());
                                chatRoomEntry0.getOffersIds().put('#' + sid, ttt.getList().stream().map(offerPayload -> offerPayload.getId().toHexString()).collect(Collectors.toList()));
                            }
                         //   logger.info("MY tags offer list :'{}'", titanic.getList());
                            return true;
                        });
            }).collectList().map(booleans -> {
                chatRoomEntry0.onPostMessage(titanic, "tags", null, "top-offers-tags-" + meId + '-' + userPayload.getRandom());
                return true;
            });
           // allMono.add(myOffers);
            TopThreeHundredOffers pitanic = new TopThreeHundredOffers();
            Mono<Boolean> myOfferUsers = topOffersStores.flatMapMany(store -> Flux.fromIterable(userPayload.getUsers())
                    .publishOn(Schedulers.boundedElastic()).map(sid -> {
                        TopThreeHundredOffers ttt = store.get('@' + sid);
                        if (ttt != null) {
                            pitanic.getList().addAll(ttt.getList());
                            chatRoomEntry0.getOffersIds().put('@' + sid, ttt.getList().stream().map(offerPayload -> offerPayload.getId().toHexString()).collect(Collectors.toList()));
                        }
                      //  logger.info("MY offer users list :'{}'", pitanic.getList());
                        return true;
                    })).collectList().map(bulls -> {
                chatRoomEntry0.onPostMessage(pitanic, "people", null, "top-offers-people-" + meId + '-' + userPayload.getRandom());
                return true;
            });

            Mono.zip(myUsers.subscribeOn(Schedulers.boundedElastic()), myTags.subscribeOn(Schedulers.boundedElastic()), myCounts.subscribeOn(Schedulers.boundedElastic()), myOffers.subscribeOn(Schedulers.boundedElastic()), myOfferUsers.subscribeOn(Schedulers.boundedElastic())).subscribe();
        } else {
            String tagging;
            if (userPayload.getIndex().equals(2)) tagging = userPayload.getUsers().get(0);
            else tagging = userPayload.getTags().get(0);
            topNewsStores.map(store -> {
                TopThreeHundredNews thn = store.get(tagging);
                if (thn != null) {
                    chatRoomEntry.getNewsIds().put(tagging, thn.getList().stream().map(newsPayload -> newsPayload.getId().toHexString()).collect(Collectors.toList()));
                    chatRoomEntry.onPostMessage(thn, tagging.charAt(0) == '@' ? "person" : "tag", null, "top-news-" + tagging + '-' + userPayload.getRandom());
                }
                return true;
            }).subscribe();
        }
        latch.countDown();
    }

    public static <K, V> CompletableFuture<ReadOnlyKeyValueStore<K, V>> toCompletableFuture(ListenableFuture<ReadOnlyKeyValueStore<K, V>> listenableFuture) {
        final CompletableFuture<ReadOnlyKeyValueStore<K, V>> completableFuture = new CompletableFuture<>();
        Futures.addCallback(listenableFuture, new FutureCallback<>() {
            @Override
            public void onSuccess(ReadOnlyKeyValueStore<K, V> result) {
                completableFuture.complete(result);
            }

            @Override
            @ParametersAreNonnullByDefault
            public void onFailure(Throwable t) {
                completableFuture.completeExceptionally(t);
            }
        }, MoreExecutors.directExecutor());

        return completableFuture;
    }

    public static <T> T waitUntilStoreIsQueryable(final String storeName,
                                                  final QueryableStoreType<T> queryableStoreType,
                                                  final KafkaStreams streams) throws InterruptedException {
        KStreamConf.startupLatch.await();
        return streams.store(StoreQueryParameters.fromNameAndType(storeName, queryableStoreType));
    }

    public <K, V> ListenableFuture<ReadOnlyKeyValueStore<K, V>> future(final String storeName) {
        return pool.submit(() -> waitUntilStoreIsQueryable(storeName, QueryableStoreTypes.keyValueStore(), this.factoryBean.getKafkaStreams()));
    }
}
