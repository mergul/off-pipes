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
import reactor.core.scheduler.Schedulers;

@Configuration
@ComponentScan(basePackageClasses = {com.streams.pipes.config.processor.ProcessorConf.class})
public class ProcessorConf {
    @Qualifier("sink")
    @Bean(name = "sink")
    public <T> Sinks.Many<ServerSentEvent<T>> sink(){
        return Sinks.many().replay().limit(5);
    }
    @Qualifier("hotFlux")
    @Bean(name = "hotFlux")
    @DependsOn({"sink"})
    public <T> Flux<ServerSentEvent<T>> hotFlux(@Autowired @Qualifier("sink") Sinks.Many<ServerSentEvent<T>> sink){
        return sink.asFlux()
                .publishOn(Schedulers.newParallel("sse-flux"));
    }
   @Bean(name = "roomEntre")
   @DependsOn({"sink", "hotFlux"})
    public <T> RoomEntre<T> roomEntre(@Autowired @Qualifier("hotFlux") Flux<ServerSentEvent<T>> hotFlux, @Autowired @Qualifier("sink") final Sinks.Many<ServerSentEvent<T>> sink){
       return new RoomEntre<T>(hotFlux, sink);
   }
}
