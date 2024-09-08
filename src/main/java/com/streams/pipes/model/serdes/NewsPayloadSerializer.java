package com.streams.pipes.model.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import com.streams.pipes.model.NewsPayload;

import javax.annotation.Nonnull;
import java.io.IOException;

public class NewsPayloadSerializer implements StreamSerializer<NewsPayload> {
    private final ObjectMapper objectMapper;

    public NewsPayloadSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public int getTypeId() {
        return MySerializationConstants.NEWS_TYPE.getId();
    }

    @Override
    public void write(@Nonnull ObjectDataOutput out, @Nonnull NewsPayload newsPayload) throws IOException {
        byte[] bytes = this.objectMapper.writeValueAsBytes(newsPayload);
        out.writeByteArray(bytes);
    }

    @Nonnull
    @Override
    public NewsPayload read(@Nonnull ObjectDataInput in) throws IOException {
        byte[] bytes = in.readByteArray();
        return this.objectMapper.readValue(bytes, NewsPayload.class);
    }
}
