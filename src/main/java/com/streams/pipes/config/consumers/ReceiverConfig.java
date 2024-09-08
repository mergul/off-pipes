package com.streams.pipes.config.consumers;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class ReceiverConfig {
  private static final Logger logger = LoggerFactory.getLogger(ReceiverConfig.class);

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, JsonNode.class);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "json");
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
//    props.put(JsonDeserializer.TYPE_MAPPINGS, "news:com.streams.pipes.model.NewsPayload,user:com.streams.pipes.model.UserPayload,offer:com.streams.pipes.model.OfferPayload");

    return props;
  }

  @Bean
  public ConsumerFactory<byte[], Object> consumerFactory() {
    try (JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>()) {
      Map<String, Object> deserProps = new HashMap<>();
      deserProps.put(JsonDeserializer.TYPE_MAPPINGS,
              "news:com.streams.pipes.model.NewsPayload, user:com.streams.pipes.model.UserPayload, offer:com.streams.pipes.model.OfferPayload");
      jsonDeserializer.configure(deserProps, false);
      return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new ByteArrayDeserializer(),
              jsonDeserializer.trustedPackages("*"));
    }
  }
  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<byte[], Object>> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<byte[], Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setCommonErrorHandler(new GlobalErrorHandler());
    factory.setConcurrency(3);
//    factory.setRecordMessageConverter(multiTypeConverter());
    factory.getContainerProperties().setPollTimeout(3000);
    return factory;
  }
//  @Bean
//  public RecordMessageConverter multiTypeConverter() {
//    StringJsonMessageConverter converter = new StringJsonMessageConverter();
//    DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
//    typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
//    typeMapper.addTrustedPackages("com.streams.pipes");
//    Map<String, Class<?>> mappings = new HashMap<>();
//    mappings.put("news", NewsPayload.class);
//    mappings.put("user", UserPayload.class);
//    mappings.put("offer", OfferPayload.class);
//    typeMapper.setIdClassMapping(mappings);
//    converter.setTypeMapper(typeMapper);
//    return converter;
//  }
}
