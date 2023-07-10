package com.streams.pipes.model.serdes;

import com.streams.pipes.model.NewsPayload;
import com.streams.pipes.model.OfferPayload;
import org.springframework.kafka.support.serializer.JsonSerde;

public class OfferPayloadSerde extends JsonSerde<OfferPayload>{
    public OfferPayloadSerde(){
        super();
        this.ignoreTypeHeaders();
    }
}
