package com.streams.pipes.config.consumers;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.lang.NonNull;

public class GlobalErrorHandler implements CommonErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class);
    @Override
    public boolean handleOne(
            Exception thrownException,
            @NonNull ConsumerRecord<?, ?> record,
            Consumer<?, ?> consumer,
            MessageListenerContainer container) {
        log.error("Global error handler for message: {}", record.value().toString());
        return true;
    }
    @Override
    public void handleOtherException(Exception thrownException, Consumer<?, ?> consumer, MessageListenerContainer container, boolean batchListener) {
        CommonErrorHandler.super.handleOtherException(thrownException, consumer, container, batchListener);
    }
}