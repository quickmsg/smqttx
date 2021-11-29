package io.github.quickmsg.core.mqtt;

import io.github.quickmsg.common.auth.PasswordAuthentication;
import io.github.quickmsg.common.config.AbstractConfiguration;
import io.github.quickmsg.common.config.Configuration;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.enums.ChannelEvent;
import io.github.quickmsg.common.handler.CacheTrafficHandlerLoader;
import io.github.quickmsg.common.handler.LazyTrafficHandlerLoader;
import io.github.quickmsg.common.handler.TrafficHandlerLoader;
import io.github.quickmsg.common.integrate.IgniteCacheRegion;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.IntegrateBuilder;
import io.github.quickmsg.common.protocol.ProtocolAdaptor;
import io.github.quickmsg.common.rule.DslExecutor;
import io.github.quickmsg.common.spi.registry.EventRegistry;
import io.github.quickmsg.common.transport.Transport;
import io.github.quickmsg.core.spi.DefaultProtocolAdaptor;
import io.github.quickmsg.dsl.RuleDslParser;
import io.github.quickmsg.interate.IgniteIntegrate;
import io.github.quickmsg.rule.source.SourceManager;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.events.EventType;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import reactor.core.scheduler.Schedulers;
import reactor.netty.resources.LoopResources;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * @author luxurong
 */
@Getter
@Setter
@Slf4j
public abstract class AbstractReceiveContext<T extends Configuration> implements ReceiveContext<T> {

    private T configuration;

    private LoopResources loopResources;

    private Transport<T> transport;

    private final ProtocolAdaptor protocolAdaptor;

    private final PasswordAuthentication passwordAuthentication;

    private final EventRegistry eventRegistry;

    private final DslExecutor dslExecutor;

    private final TrafficHandlerLoader trafficHandlerLoader;

    private final Integrate integrate;


    public AbstractReceiveContext(T configuration, Transport<T> transport) {
        AbstractConfiguration abstractConfiguration = castConfiguration(configuration);
        RuleDslParser ruleDslParser = new RuleDslParser(abstractConfiguration.getRuleChainDefinitions());
        this.configuration = configuration;
        this.transport = transport;
        this.dslExecutor = ruleDslParser.parseRule();
        this.eventRegistry = eventRegistry();
        this.protocolAdaptor = protocolAdaptor();
//        this.channelRegistry = channelRegistry();
//        this.topicRegistry = topicRegistry();
        this.loopResources = LoopResources.create("smqtt-cluster-io", configuration.getBossThreadSize(), configuration.getWorkThreadSize(), true);
        this.trafficHandlerLoader = trafficHandlerLoader();
//        this.messageRegistry = messageRegistry();
//        this.clusterRegistry = clusterRegistry();
        this.passwordAuthentication = basicAuthentication();
//        this.channelRegistry.startUp(abstractConfiguration.getEnvironmentMap());
//        this.messageRegistry.startUp(abstractConfiguration.getEnvironmentMap());
        this.integrate = integrateBuilder().newIntegrate(initConfig());
        Optional.ofNullable(abstractConfiguration.getSourceDefinitions())
                .ifPresent(sourceDefinitions -> sourceDefinitions.forEach(SourceManager::loadSource));
    }


    private TrafficHandlerLoader trafficHandlerLoader() {
        if (configuration.getGlobalReadWriteSize() == null && configuration.getChannelReadWriteSize() == null) {
            return new CacheTrafficHandlerLoader(new GlobalTrafficShapingHandler(this.loopResources.onServer(true).next()));
        } else if (configuration.getChannelReadWriteSize() == null) {
            String[] limits = configuration.getGlobalReadWriteSize().split(",");
            return new CacheTrafficHandlerLoader(new GlobalTrafficShapingHandler(this.loopResources.onServer(true),
                    Long.parseLong(limits[1]),
                    Long.parseLong(limits[0])));
        } else if (configuration.getGlobalReadWriteSize() == null) {
            String[] limits = configuration.getChannelReadWriteSize().split(",");
            return new LazyTrafficHandlerLoader(() -> new GlobalTrafficShapingHandler(this.loopResources.onServer(true),
                    Long.parseLong(limits[1]),
                    Long.parseLong(limits[0])));
        } else {
            String[] globalLimits = configuration.getGlobalReadWriteSize().split(",");
            String[] channelLimits = configuration.getChannelReadWriteSize().split(",");
            return new CacheTrafficHandlerLoader(new GlobalChannelTrafficShapingHandler(
                    this.loopResources.onServer(true),
                    Long.parseLong(globalLimits[1]),
                    Long.parseLong(globalLimits[0]),
                    Long.parseLong(channelLimits[1]),
                    Long.parseLong(channelLimits[0])));
        }
    }


    private EventRegistry eventRegistry() {
        return ChannelEvent::sender;
    }


    private PasswordAuthentication basicAuthentication() {
        AbstractConfiguration abstractConfiguration = castConfiguration(configuration);
        return Optional.ofNullable(PasswordAuthentication.INSTANCE)
                .orElse(abstractConfiguration.getReactivePasswordAuth());
    }


    private ProtocolAdaptor protocolAdaptor() {
        return Optional.ofNullable(ProtocolAdaptor.INSTANCE)
                .orElse(new DefaultProtocolAdaptor(Schedulers.newBoundedElastic(configuration.getBusinessThreadSize(), configuration.getBusinessQueueSize(), "business-io")))
                .proxy();
    }


    private AbstractConfiguration castConfiguration(T configuration) {
        return (AbstractConfiguration) configuration;
    }

    private IntegrateBuilder integrateBuilder() {
        return configuration -> new IgniteIntegrate(configuration, protocolAdaptor);
    }

    private IgniteConfiguration initConfig() {
        DataRegionConfiguration[] regionConfigurations =
                (DataRegionConfiguration[]) Arrays.stream(IgniteCacheRegion.values())
                        .map(region -> new DataRegionConfiguration()
                                .setName(region.getRegionName())
                                .setPersistenceEnabled(region.persistence())).toArray();

        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
        dataStorageConfiguration.setDataRegionConfigurations(regionConfigurations);


        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);
        igniteConfiguration.setClientMode(false);
        igniteConfiguration.setLocalHost("127.0.0.1");
        igniteConfiguration.setPeerClassLoadingEnabled(true);
        // Enable cache events.
        igniteConfiguration.setIncludeEventTypes(EventType.EVT_NODE_JOINED, EventType.EVT_NODE_LEFT, EventType.EVT_NODE_FAILED);


        // Setting up an IP Finder to ensure the client can locate the servers.
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        igniteConfiguration.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));
        return igniteConfiguration;
    }


}
