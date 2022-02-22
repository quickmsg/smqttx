package io.github.quickmsg.core.mqtt;

import io.github.quickmsg.common.ack.AckManager;
import io.github.quickmsg.common.ack.TimeAckManager;
import io.github.quickmsg.common.acl.AclManager;
import io.github.quickmsg.common.auth.PasswordAuthentication;
import io.github.quickmsg.common.config.AbstractConfiguration;
import io.github.quickmsg.common.config.BootstrapConfig;
import io.github.quickmsg.common.config.ConfigCheck;
import io.github.quickmsg.common.config.Configuration;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.enums.ChannelEvent;
import io.github.quickmsg.common.handler.CacheTrafficHandlerLoader;
import io.github.quickmsg.common.handler.LazyTrafficHandlerLoader;
import io.github.quickmsg.common.handler.TrafficHandlerLoader;
import io.github.quickmsg.common.integrate.IgniteCacheRegion;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.IntegrateBuilder;
import io.github.quickmsg.common.metric.MetricManager;
import io.github.quickmsg.common.metric.MetricManagerHolder;
import io.github.quickmsg.common.metric.local.LocalMetricManager;
import io.github.quickmsg.common.protocol.ProtocolAdaptor;
import io.github.quickmsg.common.spi.registry.EventRegistry;
import io.github.quickmsg.common.transport.Transport;
import io.github.quickmsg.core.DefaultProtocolAdaptor;
import io.github.quickmsg.common.rule.RuleDslAcceptor;
import io.github.quickmsg.core.acl.JCasBinAclManager;
import io.github.quickmsg.dsl.RuleDslParser;
import io.github.quickmsg.interate.IgniteIntegrate;
import io.github.quickmsg.metric.InfluxDbMetricFactory;
import io.github.quickmsg.metric.PrometheusMetricFactory;
import io.github.quickmsg.rule.source.SourceManager;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import reactor.core.scheduler.Schedulers;
import reactor.netty.resources.LoopResources;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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

    private final EventRegistry eventRegistry;

    private final RuleDslAcceptor ruleDslAcceptor;

    private final MetricManager metricManager;

    private final TrafficHandlerLoader trafficHandlerLoader;

    private final Integrate integrate;

    private final AckManager ackManager;

    private final AclManager aclManager;


    public AbstractReceiveContext(T configuration, Transport<T> transport) {
        AbstractConfiguration abstractConfiguration = castConfiguration(configuration);
        this.configuration = configuration;
        this.transport = transport;
        this.eventRegistry = eventRegistry();
        this.protocolAdaptor = protocolAdaptor();
        this.loopResources = LoopResources.create("smqtt-cluster-io", configuration.getBossThreadSize(), configuration.getWorkThreadSize(), true);
        this.trafficHandlerLoader = trafficHandlerLoader();
        this.integrate = integrateBuilder().newIntegrate(initConfig());
        RuleDslParser ruleDslParser = new RuleDslParser(abstractConfiguration.getRuleChainDefinitions());
        this.ruleDslAcceptor = new RuleDslAcceptor(integrate.getPipeline(), ruleDslParser.executor());
        Optional.ofNullable(abstractConfiguration.getSourceDefinitions())
                .ifPresent(sourceDefinitions -> sourceDefinitions.forEach(SourceManager::loadSource));
        this.metricManager = metricManager(abstractConfiguration.getMeterConfig());
        this.ackManager = new TimeAckManager(100, TimeUnit.MILLISECONDS, 512);
        // tod
        this.aclManager = new JCasBinAclManager(null);
        Optional.ofNullable(abstractConfiguration.getSourceDefinitions()).ifPresent(sourceDefinitions -> sourceDefinitions.forEach(SourceManager::loadSource));

    }


    private TrafficHandlerLoader trafficHandlerLoader() {
        if (configuration.getGlobalReadWriteSize() == null && configuration.getChannelReadWriteSize() == null) {
            return new CacheTrafficHandlerLoader(new GlobalTrafficShapingHandler(this.loopResources.onServer(true).next(), 60 * 1000));
        } else if (configuration.getChannelReadWriteSize() == null) {
            String[] limits = configuration.getGlobalReadWriteSize().split(",");
            return new CacheTrafficHandlerLoader(new GlobalTrafficShapingHandler(this.loopResources.onServer(true), Long.parseLong(limits[1]), Long.parseLong(limits[0]), 60 * 1000));
        } else if (configuration.getGlobalReadWriteSize() == null) {
            String[] limits = configuration.getChannelReadWriteSize().split(",");
            return new LazyTrafficHandlerLoader(() -> new GlobalTrafficShapingHandler(this.loopResources.onServer(true), Long.parseLong(limits[1]), Long.parseLong(limits[0]), 60 * 1000));
        } else {
            String[] globalLimits = configuration.getGlobalReadWriteSize().split(",");
            String[] channelLimits = configuration.getChannelReadWriteSize().split(",");
            return new CacheTrafficHandlerLoader(new GlobalChannelTrafficShapingHandler(this.loopResources.onServer(true), Long.parseLong(globalLimits[1]), Long.parseLong(globalLimits[0]), Long.parseLong(channelLimits[1]), Long.parseLong(channelLimits[0]), 60 * 1000));
        }
    }


    private EventRegistry eventRegistry() {
        return ChannelEvent::sender;
    }


    private ProtocolAdaptor protocolAdaptor() {
        return Optional.ofNullable(ProtocolAdaptor.INSTANCE)
                .orElseGet(() -> new DefaultProtocolAdaptor(Schedulers.newBoundedElastic(configuration.getBusinessThreadSize(), configuration.getBusinessQueueSize(), "business-io"))).proxy();
    }

    private MetricManager metricManager(BootstrapConfig.MeterConfig meterConfig) {
        ConfigCheck.checkMeterConfig(meterConfig);
        return MetricManagerHolder.setMetricManager(Optional.ofNullable(meterConfig).map(config -> {
            switch (config.getMeterType()) {
                case INFLUXDB:
                    return new InfluxDbMetricFactory(config).getMetricManager();
                case PROMETHEUS:
                    return new PrometheusMetricFactory(config).getMetricManager();
                default:
                    return new LocalMetricManager();
            }
        }).orElseGet(LocalMetricManager::new));
    }

    private AbstractConfiguration castConfiguration(T configuration) {
        return (AbstractConfiguration) configuration;
    }

    private IntegrateBuilder integrateBuilder() {
        return configuration -> new IgniteIntegrate(configuration, protocolAdaptor);
    }

    private IgniteConfiguration initConfig() {
        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
        dataStorageConfiguration.setDataRegionConfigurations(getDataRegionConfigurations(IgniteCacheRegion.values()));
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);
        igniteConfiguration.setGridLogger(new Slf4jLogger());
        igniteConfiguration.setLocalHost("127.0.0.1");
        igniteConfiguration.setClientMode(false);
        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Arrays.asList("127.0.0.1", "127.0.0.1:47500..47509"));
        spi.setIpFinder(ipFinder);

        igniteConfiguration.setDiscoverySpi(spi);
        return igniteConfiguration;
    }

    private DataRegionConfiguration[] getDataRegionConfigurations(IgniteCacheRegion[] values) {
        DataRegionConfiguration[] regionConfigurations = new DataRegionConfiguration[values.length];
        for (int i = 0; i < values.length; i++) {
            regionConfigurations[i] = new DataRegionConfiguration()
                    .setName(values[i].getRegionName())
                    .setPersistenceEnabled(values[i].persistence());
        }
        return regionConfigurations;
    }


}
