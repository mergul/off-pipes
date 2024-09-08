package com.streams.pipes.config.hazelcast;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.config.*;
import com.hazelcast.jet.config.JetConfig;
import com.hazelcast.spring.context.SpringAware;
import com.hazelcast.spring.context.SpringManagedContext;
import com.streams.pipes.model.NewsPayload;
import com.streams.pipes.model.UserPayload;
import com.streams.pipes.model.serdes.NewsPayloadSerializer;
import com.streams.pipes.model.serdes.UserPayloadSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@SpringAware
public class HazelcastConfig {
    private final Logger log = LoggerFactory.getLogger(HazelcastConfig.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public SpringManagedContext managedContext() {
        return new SpringManagedContext();
    }

    @Bean
    public Config myConfig() {
        Config config = new Config();
        config.setNetworkConfig(getHazelcastNetworkConfig());
        config.setClusterName("hazelcast-cluster");
        config.setInstanceName("hazelcast-template");
        config.addListenerConfig(new ListenerConfig("com.streams.pipes.service.ClusterMembershipListener"));
        config.addListenerConfig(new ListenerConfig("com.streams.pipes.service.MyDistributedObjectListener"));
        config.setPartitionGroupConfig(getPartitionGroupConfig());
        config.setProperty("hazelcast.health.monitoring.level","NOISY");
        config.addMapConfig(getHazelcastMapConfig("myNewsMap"));
        config.addMapConfig(getHazelcastMapConfig("myUserMap"));
        config.addMapConfig(getHazelcastMapConfig("myNewsCounts"));
        // config.getSerializationConfig().addSerializerConfig(serializerConfig());
        //.setMergePolicyConfig(new MergePolicyConfig("com.hazelcast.spi.merge.PassThroughMergePolicy", 110))

        SerializationConfig serializationConfig = config.getSerializationConfig();
        serializationConfig.setEnableCompression(true);
//        serializationConfig.setGlobalSerializerConfig(new GlobalSerializerConfig().setOverrideJavaSerialization(true).setClassName("com.streams.pipes.model.serdes.GlobalStreamSerializer"));

        Collection<SerializerConfig> serializerConfigs = serializationConfig.getSerializerConfigs();
        serializerConfigs.add(new SerializerConfig().setTypeClass(NewsPayload.class)
                .setImplementation(new NewsPayloadSerializer(objectMapper)));
        serializerConfigs.add(new SerializerConfig().setTypeClass(UserPayload.class)
                .setImplementation(new UserPayloadSerializer(objectMapper)));
        config.setManagedContext(managedContext());

        // Caching settings
        config.addCacheConfig(new CacheSimpleConfig()
                .setName("myNewsCache")
                .setEventJournalConfig(getEventJournalCacheConfig()));

        // Configuring distributed executor service
        config.addExecutorConfig(new ExecutorConfig()
                .setName("myExecutorService")
                .setPoolSize(10)
                .setQueueCapacity(100)
                .setStatisticsEnabled(true)
                .setSplitBrainProtectionName("splitbrainprotectionname")
        ); // Number of threads in the executor service

        // Management Center configuration
        ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig();
        managementCenterConfig.setConsoleEnabled(true)
                .addTrustedInterface("http://localhost:8080/mancenter");
        config.setManagementCenterConfig(managementCenterConfig);

        // Jet configuration
        JetConfig jetConfig = new JetConfig();
        jetConfig.setCooperativeThreadCount(8);
        jetConfig.setEnabled(true);
        jetConfig.setResourceUploadEnabled(true);
        config.setJetConfig(jetConfig);

        return config;
    }

//    @Bean
//    public HazelcastInstance hazelcastInstance() {
//        HazelcastInstance hazelCastInstance =
//                Hazelcast.getHazelcastInstanceByName("hazelcast-template");
//        if (hazelCastInstance != null) {
//            log.debug("Hazelcast already initialized");
//            return hazelCastInstance;
//        }
////        IMap<String, String> map = hazelCastInstance.getMap( "somemap" );
////        map.addEntryListener( new MyMapEntryListener(), true );
//        return Hazelcast.newHazelcastInstance(myConfig());
//    }

    private MapConfig getHazelcastMapConfig(String mapName){

        MapConfig mapConfig = new MapConfig();

        mapConfig.setName(mapName);
        mapConfig.setBackupCount(2);
        mapConfig.setAsyncBackupCount(1);

        mapConfig.setMaxIdleSeconds(3600);
        mapConfig.setTimeToLiveSeconds(3600);
        mapConfig.setEventJournalConfig(getEventJournalMapConfig());
        EvictionConfig evictionConfig = new EvictionConfig();
        evictionConfig.setEvictionPolicy(EvictionPolicy.LRU);
        evictionConfig.setMaxSizePolicy(MaxSizePolicy.PER_PARTITION);
        evictionConfig.setSize(1000);

        mapConfig.setEvictionConfig(evictionConfig);

        mapConfig.setMetadataPolicy(MetadataPolicy.CREATE_ON_UPDATE);
        mapConfig.setReadBackupData(false);

        mapConfig.addEntryListenerConfig(
                new EntryListenerConfig( "com.streams.pipes.service.MyMapEntryListener",
                        true, false ));

        return mapConfig;
    }

    private NetworkConfig getHazelcastNetworkConfig(){

        NetworkConfig networkConfig = new NetworkConfig().setPort(5910)
                .setPortAutoIncrement(true);
        // networkConfig.getInterfaces().addInterface("127.0.0.1");
        JoinConfig joinConfig = new JoinConfig();
        TcpIpConfig tcpIpConfig = new TcpIpConfig();
        tcpIpConfig.setConnectionTimeoutSeconds(30);
        tcpIpConfig.setEnabled(true);

        List<String> memberList = new ArrayList<>();
        memberList.add("127.0.0.1:5910");
        tcpIpConfig.setMembers(memberList);
        // joinConfig.getTcpIpConfig().addMember("127.0.0.1:10572");
        joinConfig.setTcpIpConfig(tcpIpConfig);

        MulticastConfig multicastConfig = new MulticastConfig();
        multicastConfig.setMulticastTimeoutSeconds(30);
        multicastConfig.setMulticastTimeToLive(255);
        multicastConfig.setEnabled(false);

        joinConfig.setMulticastConfig(multicastConfig);

        networkConfig.setJoin(joinConfig);

        return networkConfig;
    }
    private PartitionGroupConfig getPartitionGroupConfig(){
        return new PartitionGroupConfig().setEnabled(true)
                .setGroupType(PartitionGroupConfig.MemberGroupType.PER_MEMBER);
    }

    private EventJournalConfig getEventJournalMapConfig() {
        return new EventJournalConfig()
                .setEnabled(true)
                .setCapacity(5000)
                .setTimeToLiveSeconds(20);
    }

    private EventJournalConfig getEventJournalCacheConfig() {
        return new EventJournalConfig()
                .setEnabled(true)
                .setCapacity(10000)
                .setTimeToLiveSeconds(0);
    }

}
