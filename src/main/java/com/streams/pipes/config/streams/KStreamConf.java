package com.streams.pipes.config.streams;

import com.streams.pipes.chat.RoomEntre;
import com.streams.pipes.model.*;
import com.streams.pipes.model.serdes.*;
import com.streams.pipes.service.Sender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KStreamConf {
 //   private static final Logger logger = LoggerFactory.getLogger(KStreamConf.class);
    private static final String TOP_NEWS_STORE = "windowed-news-stores";
    private static final String TOP_USERS_STORE = "windowed-users-stores";
    private static final String NEWS_STORE = "stream-news-stores";
    private static final String USER_STORE = "stream-users-stores";
    private static final String MY_USER_STORE = "stream-musers-stores";
    private static final String TOP_OFFERS_STORE = "windowed-offers-stores";

    @Value("${kafka.topics.sender-topics}")
    private String senderTopic;

    @Value("${kafka.topics.receiver-topics}")
    private String receiverTopic;

    @Value("${kafka.topics.offerviews-in}")
    private String offerviewsTopics;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    public final static CountDownLatch startupLatch = new CountDownLatch(1);
    private final ObjectMapper objectMapper;
    private final RoomEntre<?> entre;
    private final Sender kafkaSender;

    public KStreamConf(ObjectMapper objectMapper, RoomEntre<?> entre/*, @Qualifier(value = "miProcessor") UnicastProcessor<BalanceRecord> eventPublisher*/, Sender kafkaSender) {
        this.objectMapper = objectMapper;
        this.entre = entre;
        this.kafkaSender = kafkaSender;
    }
    @Bean
    public RecordMessageConverter converter() {
        return new ByteArrayJsonMessageConverter();
    }
    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streamingStreamName");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class);
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/data");
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 5);
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        props.put(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG, 1);
        props.put(StreamsConfig.producerPrefix(ProducerConfig.METADATA_MAX_AGE_CONFIG), 500);
        props.put(StreamsConfig.consumerPrefix(ConsumerConfig.METADATA_MAX_AGE_CONFIG), 500);
        props.put(StreamsConfig.consumerPrefix(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG), true);
        props.put(StreamsConfig.consumerPrefix(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG), "latest");
        props.put(StreamsConfig.consumerPrefix(ConsumerConfig.GROUP_ID_CONFIG), "share-group");
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, JsonNode.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public StreamsBuilderFactoryBeanConfigurer configurer() {
        return fb -> fb.setStateListener((newState, oldState) -> {
            if (newState == KafkaStreams.State.RUNNING && oldState == KafkaStreams.State.REBALANCING) {
                startupLatch.countDown();
            }
             else if (newState != KafkaStreams.State.RUNNING) {
            //    logger.info("State is => Not Ready");
             }
           // logger.info("State transition from " + oldState + " to " + newState);
        });
    }

    @SuppressWarnings("unchecked")
    @Bean
    public KStream<byte[], RecordSSE> kStream(StreamsBuilder kStreamBuilder) {
        KStream<byte[], NewsPayload> previewsInput = kStreamBuilder.stream(receiverTopic, Consumed.with(new Serdes.ByteArraySerde(), new NewsPayloadSerde()));
        KStream<byte[], OfferPayload> offerviews = kStreamBuilder.stream(offerviewsTopics, Consumed.with(new Serdes.ByteArraySerde(), new OfferPayloadSerde()));

        KStream<OfferPayload, OfferPayload> offerstream = offerviews
                .flatMapValues(news -> {
                    // logger.info("Consuming stream value --> {}", news.toString());
                    news.getTags().add("main");
                    news.getTags().add("@" + news.getNewsOwnerId());
                    news.getTags().add("@@" + news.getOwnerId());
                    news.getTags().add("#" + news.getNewsId());
                    return news.getTags().stream().map(s -> OfferPayload.from(news)
                            .withTags(Collections.singletonList(s)).build()).collect(Collectors.toList());
                })
                .map((key, value) -> {
                    OfferPayload clone = OfferPayload.from(value).build();
                    return new KeyValue<>(clone, clone);
                });
        //count separated tag news
        KTable<Windowed<OfferPayload>, Long> offerOut = offerstream
                .groupByKey(Grouped.with(new OfferPayloadSerde(), new OfferPayloadSerde()))
                .windowedBy(TimeWindows.of(Duration.ofHours(6L)))
                .count();
                //.suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()));

        // count highest news by tag
        offerOut.groupBy((key, count) -> {
                    String windowedIndustry =
                            key.key().getTags() != null ? key.key().getTags().get(0).trim() : "tag";
                    OfferPayload viewStats = OfferPayload.from(key.key()).withCount(count).build();
                    return new KeyValue<>(windowedIndustry, viewStats);
                }
                , Grouped.with(Serdes.String(), new OfferPayloadSerde())
        ).aggregate(
                // the initializer
                TopThreeHundredOffers::new,
                // the "add" aggregator
                (aggKey, value, aggregate) -> {
                    aggregate.add(value);
                    return aggregate;
                },
                (aggKey, value, aggregate) -> {
                    aggregate.remove(value);
                    return aggregate;
                },
                Materialized.<String, TopThreeHundredOffers, KeyValueStore<Bytes, byte[]>>as(TOP_OFFERS_STORE)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(new TopThreeHundredOfferSerde(objectMapper))
        ).toStream((key, value) -> {
            RoomEntre<TopThreeHundredOffers> chatRoomEntry= (RoomEntre<TopThreeHundredOffers>) this.entre;
            List<String> oldList = chatRoomEntry.getOffersIds().get(key);
            if (oldList!=null) {
                List<OfferPayload> diffList = value.getList().stream().filter(s->!oldList.contains(s.getId().toHexString())).collect(Collectors.toList());
                if (diffList.size() != 0) {
                    TopThreeHundredOffers ttmOffers=new TopThreeHundredOffers();
                    ttmOffers.getList().addAll(diffList);
                    List<String> newList = value.getList().stream().map(offerPayload -> offerPayload.getId().toHexString()).collect(Collectors.toList());
                    if (key.charAt(0) == '@') {
                        if (key.charAt(1)=='@')
                            chatRoomEntry.onPostMessage(ttmOffers, "my", new Date(), "top-offers-" + key);
                        else chatRoomEntry.onPostMessage(ttmOffers, "me", new Date(), "top-offers-" + key);
                    } else if (key.charAt(0) == ('m')) {
                        chatRoomEntry.onPostMessage(ttmOffers, key, new Date(), "top-offers");
                    } else {
                        chatRoomEntry.onPostMessage(ttmOffers, "tag", new Date(), "top-offers-" + key);
                    }
                    chatRoomEntry.getOffersIds().put(key, newList);
                }
            }
            return KeyValue.pair(key, value);
        });

        KStream<NewsPayload, NewsPayload> stream = previewsInput
                .flatMapValues(news -> {
                   // logger.info("Consuming stream value --> {}", news.toString());
                    news.getTags().add("main");
                    news.getTags().add("@" + news.getNewsOwnerId());
                    return news.getTags().stream().map(s -> NewsPayload.from(news)
                            .withTags(Collections.singletonList(s)).build()).collect(Collectors.toList());
                })
                .map((key, value) -> {
                    NewsPayload clone = NewsPayload.from(value).build();
                    return new KeyValue<>(clone, clone);
                });
        //count separated tag news
        KTable<Windowed<NewsPayload>, Long> out = stream
                .groupByKey(Grouped.with(new NewsPayloadSerde(), new NewsPayloadSerde()))
                .windowedBy(TimeWindows.of(Duration.ofHours(6L)))
                .count();
               // .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()));
        // Serialized.with(new NewsPayloadSerde(), new NewsPayloadSerde())
        // count highest news by tag
        out.groupBy((key, count) -> {
                    String windowedIndustry =
                            key.key().getTags() != null ? key.key().getTags().get(0).trim() : "tag";
                    NewsPayload viewStats = NewsPayload.from(key.key()).withCount(count).build();
                    return new KeyValue<>(windowedIndustry, viewStats);
                }
                , Grouped.with(Serdes.String(), new NewsPayloadSerde())
        ).aggregate(
                // the initializer
                TopThreeHundredNews::new,
                // the "add" aggregator
                (aggKey, value, aggregate) -> {
                    aggregate.add(value);
                    return aggregate;
                },
                (aggKey, value, aggregate) -> {
                    aggregate.remove(value);
                    return aggregate;
                },
                Materialized.<String, TopThreeHundredNews, KeyValueStore<Bytes, byte[]>>as(TOP_NEWS_STORE)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(new TopThreeHundredSerde(objectMapper))
        ).toStream((key, value) -> {
            RoomEntre<TopThreeHundredNews> chatRoomEntry= (RoomEntre<TopThreeHundredNews>) this.entre;
            List<String> oldList = chatRoomEntry.getNewsIds().get(key);
            if (oldList!=null) {
                List<NewsPayload> diffList = value.getList().stream().filter(s->!oldList.contains(s.getId().toHexString())).collect(Collectors.toList());
                if (diffList.size() != 0) {
                    TopThreeHundredNews tthNews=new TopThreeHundredNews();
                    tthNews.getList().addAll(diffList);
                    List<String> newList = value.getList().stream().map(newsPayload -> newsPayload.getId().toHexString()).collect(Collectors.toList());
                    if (key.charAt(0) == '@') {
                       chatRoomEntry.onPostMessage(tthNews, "person", new Date(), "top-news-" + key);
                    } else if (key.charAt(0) == ('m')) {
                       chatRoomEntry.onPostMessage(tthNews, key, new Date(), "top-news");
                    } else {
                       chatRoomEntry.onPostMessage(tthNews, "tag", new Date(), "top-news-" + key);
                    }
                    chatRoomEntry.getNewsIds().put(key, newList);
                }
            }
            return KeyValue.pair(key, value);
        });

        out.toStream()
                .map((newsPayloadWindowed, aLong) -> KeyValue.pair(newsPayloadWindowed.key().getTags().get(0), aLong))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
                .count().groupBy((key, count) -> {
                  //  logger.info("Consuming stream key --> {}", key);
                    String windowedIndustry = "top-tags";
                    RecordSSE viewStats = new RecordSSE(key, count);
                    return new KeyValue<>(windowedIndustry, viewStats);
                }
                , Grouped.with(Serdes.String(), new RecordSSESerde())
        ).aggregate(
                // the initializer
                TopHundredNews::new,
                // the "add" aggregator
                (aggKey, value, aggregate) -> {
                    aggregate.add(value);
                    return aggregate;
                },
                (aggKey, value, aggregate) -> {
                    aggregate.remove(value);
                    return aggregate;
                },
                Materialized.<String, TopHundredNews, KeyValueStore<Bytes, byte[]>>as(TOP_USERS_STORE)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(new TopHundredSerde(objectMapper))
        ).toStream((key, value) -> {
            List<String> maList = value.getList().stream().map(RecordSSE::getKey).collect(Collectors.toList());
            RoomEntre<TopHundredNews> chatRoomEntry= (RoomEntre<TopHundredNews>) this.entre;
            if (chatRoomEntry.getNewsIds().get(key)==null || !chatRoomEntry.getNewsIds().get(key).containsAll(maList)) {
                TopHundredNews thn = new TopHundredNews();
                value.forEach(recordSSE -> {
                    if (recordSSE.getKey().charAt(0) == ('#')) {
                        thn.add(recordSSE);
                    }
                });
                chatRoomEntry.onPostMessage(thn, key, new Date(), "top-tags");
                chatRoomEntry.getNewsIds().put(key, maList);
            }
            return KeyValue.pair(key, value);
        });
        //latest user and news counts by unique ids
        KStream<byte[], Long> userCounts = previewsInput
                .map((k, v) -> KeyValue.pair(('@' + v.getNewsOwnerId()).getBytes(), 1L))
                .groupByKey()
                .count(Materialized.<byte[], Long, KeyValueStore<Bytes, byte[]>>as(USER_STORE)
                        .withKeySerde(Serdes.ByteArray())
                        .withValueSerde(Serdes.Long())
                ).toStream((key, value) -> {
                    RoomEntre<RecordSSE> chatRoomEntry= (RoomEntre<RecordSSE>) this.entre;
                    chatRoomEntry.onPostMessage(new RecordSSE(new String(key), value), new String(key), new Date(), "user-counts-" + new String(key));
                    return key;
                });
        KStream<byte[], Long> totalCount = userCounts
                .map((key, value) -> KeyValue.pair("@total".getBytes(), value))
                .groupByKey()
                .count(Materialized.<byte[], Long, KeyValueStore<Bytes, byte[]>>as(MY_USER_STORE)
                                .withKeySerde(Serdes.ByteArray())
                                .withValueSerde(Serdes.Long())
                ).toStream();
        // KStream<byte[], Long> newsCounts =
        previewsInput.mapValues(s -> 1L)
                .groupByKey()
                .count(Materialized.<byte[], Long, KeyValueStore<Bytes, byte[]>>as(NEWS_STORE)
                        .withKeySerde(Serdes.ByteArray())
                        .withValueSerde(Serdes.Long()))
                .toStream((key, value) -> {
                    RoomEntre<RecordSSE> chatRoomEntry= (RoomEntre<RecordSSE>) this.entre;
                    chatRoomEntry.onPostMessage(new RecordSSE(new String(key), value), new String(key), new Date(), "user-counts");
                    return key;
                });

        return totalCount.map((key, value) -> KeyValue
                .pair(key, new RecordSSE(new String(key), value)));
    }
}
