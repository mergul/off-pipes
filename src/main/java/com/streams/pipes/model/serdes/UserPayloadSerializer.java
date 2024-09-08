package com.streams.pipes.model.serdes;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import com.streams.pipes.model.UserPayload;

import javax.annotation.Nonnull;
import java.io.IOException;

public class UserPayloadSerializer implements StreamSerializer<UserPayload> {
    private final ObjectMapper objectMapper;

    public UserPayloadSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @Override
    public int getTypeId() {
        return MySerializationConstants.USER_TYPE.getId();
    }

    @Override
    public void write(@Nonnull ObjectDataOutput out, @Nonnull UserPayload userPayload) throws IOException {
        byte[] bytes = this.objectMapper.writeValueAsBytes(userPayload);
        out.writeByteArray(bytes);
    }

    @Nonnull
    @Override
    public UserPayload read(@Nonnull ObjectDataInput in) throws IOException {
        byte[] bytes = in.readByteArray();
        return this.objectMapper.readValue(bytes, UserPayload.class);
    }
}
