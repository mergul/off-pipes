//package com.streams.pipes.config.consumers;
//
//import com.streams.pipes.model.NewsPayload;
//import com.streams.pipes.model.OfferPayload;
//import com.streams.pipes.model.UserPayload;
//import org.springframework.kafka.annotation.KafkaHandler;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;

//@Component
//@KafkaListener(id = "multiGroup", topics = "multitype")
//public class MultiTypeKafkaListener {
//
//    @KafkaHandler
//    public void handleUser(UserPayload userPayload) {
//        System.out.println("userPayload received: " + userPayload);
//    }
//
//    @KafkaHandler
//    public void handleNews(NewsPayload newsPayload) {
//        System.out.println("newsPayload received: " + newsPayload);
//    }
//
//    @KafkaHandler
//    public void handleOffer(OfferPayload offerPayload) {
//        System.out.println("offerPayload received: " + offerPayload);
//    }
//
//    @KafkaHandler(isDefault = true)
//    public void unknown(Object object) {
//        System.out.println("Unkown type received: " + object);
//    }
//}