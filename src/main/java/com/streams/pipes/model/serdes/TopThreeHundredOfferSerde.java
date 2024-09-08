package com.streams.pipes.model.serdes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streams.pipes.model.OfferPayload;
import com.streams.pipes.model.TopThreeHundredOffers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
    @JsonComponent
    public class TopThreeHundredOfferSerde implements Serde<TopThreeHundredOffers> {

        private final ObjectMapper objectMapper;
        public TopThreeHundredOfferSerde(@Qualifier("userMapper") ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public void configure(final Map<String, ?> map, final boolean b) {

        }

        @Override
        public void close() {

        }

        @Override
        public Serializer<TopThreeHundredOffers> serializer() {
            return new Serializer<TopThreeHundredOffers>() {
                @Override
                public void configure(final Map<String, ?> map, final boolean b) {
                }

                @Override
                public byte[] serialize(final String s, final TopThreeHundredOffers topThreeHundredOffers) {
                    try {
                        List<OfferPayload> list= new ArrayList<>();
                        for (OfferPayload songPlayCount : topThreeHundredOffers) {
                            list.add(songPlayCount);
                        }
                        return objectMapper.writeValueAsBytes(list);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void close() {

                }
            };
        }

        @Override
        public Deserializer<TopThreeHundredOffers> deserializer() {
            return new Deserializer<TopThreeHundredOffers>() {
                @Override
                public void configure(final Map<String, ?> map, final boolean b) {

                }

                @Override
                public TopThreeHundredOffers deserialize(final String s, final byte[] bytes) {
                    if (bytes == null || bytes.length == 0) {
                        return null;
                    }
                    final TopThreeHundredOffers result = new TopThreeHundredOffers();
                    try {
                        List<OfferPayload> newsList=objectMapper
                                .readValue(bytes, new TypeReference<List<OfferPayload>>() { });
                        for (OfferPayload news : newsList){
                            result.add(news);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return result;
                }

                @Override
                public void close() {

                }
            };
        }
    }

