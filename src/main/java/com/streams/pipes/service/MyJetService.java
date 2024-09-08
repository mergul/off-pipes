package com.streams.pipes.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.internal.util.MapUtil;
import com.hazelcast.jet.JetService;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.aggregate.AggregateOperations;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.kafka.KafkaSources;
import com.hazelcast.jet.pipeline.*;
import com.hazelcast.map.IMap;
import com.hazelcast.spring.context.SpringAware;
import com.streams.pipes.model.NewsPayload;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.Map.entry;

@Service
@SpringAware
@JsonIgnoreProperties(ignoreUnknown = true)
public class MyJetService {
    private static final Logger logger = LoggerFactory.getLogger(Sender.class);

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private IMap<String, NewsPayload> myNewsMap;

    @Autowired
    private IMap<String, Long> myNewsCounts;

    @Value("${kafka.topics.receiver-topics}")
    private static String receiverTopic;

    public Mono<Void> startWordCountJob() {
        logger.info("Starting Hazelcast Jet Job");
        IExecutorService executorService = hazelcastInstance.getExecutorService("myExecutorService");
        ExecutionCallback<Void> executionCallback = new ExecutionCallback<Void>() {
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }

            public void onResponse(Void response) {
                System.out.println("Result: " + response);
            }
        };

        Future<Void> myFuture = executorService.submit(new WordCountJob());
        return getMonoFromFuture(myFuture);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @SpringAware
    private static class WordCountJob implements Callable<Void>, HazelcastInstanceAware, Serializable {
        @Serial
        private static final long serialVersionUID = 5851289963628278937L;
        private transient HazelcastInstance hazelcastInstance;
        private transient IMap<String, NewsPayload> myNewsMap;
        private transient IMap<String, Long> myNewsCounts;

        @Override
        public Void call() {
            logger.info("Starting Hazelcast Jet Job by calling");
            Pipeline pipeline = Pipeline.create();

            StreamStage<Map.Entry<byte[], NewsPayload>> streamStageNews =
                    pipeline.readFrom(KafkaSources.<byte[], NewsPayload>kafka(props("bootstrap.servers", "localhost:9092",
                                    "key.deserializer", ByteArrayDeserializer.class.getCanonicalName(),
                                    "value.deserializer", JsonDeserializer.class.getCanonicalName(),
                                    "auto.offset.reset", "earliest"), receiverTopic))
                            .withoutTimestamps()
                            .map(r -> entry(r.getKey(), r.getValue()));
            streamStageNews
                    .window(WindowDefinition.tumbling(TimeUnit.SECONDS.toMillis(1)))
                    .groupingKey(Map.Entry::getKey)
                    .aggregate(AggregateOperations.counting())
                    .map(res -> MapUtil.entry(new String(res.getKey(), StandardCharsets.UTF_8), res.getValue()))
                    .writeTo(Sinks.map(myNewsCounts));


            JetService jetService = hazelcastInstance.getJet();
            Job job = jetService.newJobIfAbsent(pipeline, new JobConfig().addClass(MyJetService.class).addClass(WordCountJob.class).addClass(NewsPayload.class));
            job.addStatusListener(event ->
                    logger.info("Job status changed: %s -> %s. User requested? %b%n", event.getPreviousStatus(),
                            event.getNewStatus(),
                            event.isUserRequested())
            );
            job.join();
            return null;
        }

        @Override
        @Autowired
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            this.hazelcastInstance = hazelcastInstance;
        }

        @Autowired
        public void setMyNewsMap(IMap<String, NewsPayload> myNewsMap) {
            this.myNewsMap = myNewsMap;
        }

        @Autowired
        public void setMyNewsCounts(IMap<String, Long> myNewsCounts) {
            this.myNewsCounts = myNewsCounts;
        }

        private static Properties props(String... kvs) {
            final Properties props = new Properties();
            for (int i = 0; i < kvs.length; ) {
                props.setProperty(kvs[i++], kvs[i++]);
            }
            return props;
        }
    }

    public Mono<Void> getMonoFromFuture(Future<Void> future) {
        return Mono.fromCallable(() -> {
            try {
                return future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
/*
pipeline.readFrom(Sources.<String, NewsPayload>mapJournal(myNewsMap,
                            JournalInitialPosition.START_FROM_CURRENT))
                    .withIngestionTimestamps()
                    .window(WindowDefinition.tumbling(TimeUnit.SECONDS.toMillis(1)))
                    .groupingKey(Map.Entry::getKey)
                    .aggregate(AggregateOperations.counting())
                    .map(res -> MapUtil.entry(res.getKey(), res.getValue()))
                    .writeTo(Sinks.map(myNewsCounts));
 */