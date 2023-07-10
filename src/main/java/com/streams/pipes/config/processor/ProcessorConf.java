package com.streams.pipes.config.processor;

import com.streams.pipes.chat.RoomEntre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Configuration
@ComponentScan(basePackageClasses = {com.streams.pipes.config.processor.ProcessorConf.class})
public class ProcessorConf {
    @Qualifier("sink")
    @Bean(name = "sink")
    public <T> Sinks.Many<ServerSentEvent<T>> sink(){
        return Sinks.unsafe().many().replay().limit(2);
    }
    @Qualifier("hotFlux")
    @Bean(name = "hotFlux")
    @DependsOn({"sink"})
    public <T> Flux<ServerSentEvent<T>> hotFlux(@Autowired @Qualifier("sink") Sinks.Many<ServerSentEvent<T>> sink){
       // Sinks.Many<ServerSentEvent<T>> sink = sink();
        return sink.asFlux();
    }
   @Bean(name = "roomEntre")
   @DependsOn({"sink", "hotFlux"})
    public <T> RoomEntre<T> roomEntre(@Autowired @Qualifier("hotFlux") Flux<ServerSentEvent<T>> hotFlux, @Autowired @Qualifier("sink") final Sinks.Many<ServerSentEvent<T>> sink){
       //Sinks.Many<ServerSentEvent<T>> sink = sink();
       //Flux<ServerSentEvent<T>> hotFlux = sink.asFlux();
       return new RoomEntre<T>(hotFlux, sink);
   }
}
