package io.github.quickmsg.core.mqtt;

import io.github.quickmsg.common.acl.AclManager;
import io.github.quickmsg.common.auth.AuthManager;
import io.github.quickmsg.common.config.*;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.handler.CacheTrafficHandlerLoader;
import io.github.quickmsg.common.handler.LazyTrafficHandlerLoader;
import io.github.quickmsg.common.handler.TrafficHandlerLoader;
import io.github.quickmsg.common.integrate.IgniteCacheRegion;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.IntegrateBuilder;
import io.github.quickmsg.common.log.LogManager;
import io.github.quickmsg.common.metric.MetricFactory;
import io.github.quickmsg.common.metric.MetricManager;
import io.github.quickmsg.common.metric.MetricManagerHolder;
import io.github.quickmsg.common.metric.local.LocalMetricManager;
import io.github.quickmsg.common.protocol.ProtocolAdaptor;
import io.github.quickmsg.common.retry.RetryManager;
import io.github.quickmsg.common.retry.TimeAckManager;
import io.github.quickmsg.common.transport.Transport;
import io.github.quickmsg.common.utils.ServerUtils;
import io.github.quickmsg.core.DefaultProtocolAdaptor;
import io.github.quickmsg.core.acl.JCasBinAclManager;
import io.github.quickmsg.core.auth.AuthManagerFactory;
import io.github.quickmsg.dsl.RuleDslExecutor;
import io.github.quickmsg.dsl.RuleDslParser;
import io.github.quickmsg.interate.IgniteIntegrate;
import io.github.quickmsg.rule.source.SourceManager;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ignite.configuration.*;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import reactor.netty.resources.LoopResources;

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


    private final MetricManager metricManager;

    private final TrafficHandlerLoader trafficHandlerLoader;

    private final Integrate integrate;

    private final RetryManager retryManager;

    private final AclManager aclManager;

    private final AuthManager authManager;

    private final LogManager logManager;

    private final RuleDslExecutor ruleDslExecutor;


    public AbstractReceiveContext(T configuration, Transport<T> transport) {
        this.logManager = new LogManager(ServerUtils.serverIp);
        AbstractConfiguration abstractConfiguration = castConfiguration(configuration);
        this.configuration = configuration;
        this.transport = transport;
        this.protocolAdaptor = protocolAdaptor(abstractConfiguration.getBusinessQueueSize(), abstractConfiguration.getBusinessThreadSize());
        this.loopResources = LoopResources.create("smqttx-cluster-io", configuration.getBossThreadSize(), configuration.getWorkThreadSize(), true);
        this.trafficHandlerLoader = trafficHandlerLoader();
        this.integrate = integrateBuilder().newIntegrate(initConfig(abstractConfiguration.getClusterConfig()));
        RuleDslParser ruleDslParser = new RuleDslParser(abstractConfiguration.getRuleChainDefinitions());
        this.ruleDslExecutor = ruleDslParser.executor();
        this.metricManager = metricManager(abstractConfiguration.getMeterConfig());
        this.retryManager = new TimeAckManager(100, TimeUnit.MILLISECONDS, 512, 5, 5);
        this.aclManager = new JCasBinAclManager(integrate.getCache(IgniteCacheRegion.CONFIG));
        this.authManager = authManagerFactory().provider(abstractConfiguration.getAuthConfig()).getAuthManager();
        Optional.ofNullable(abstractConfiguration.getSourceDefinitions())
                .ifPresent(sourceDefinitions -> sourceDefinitions.forEach(SourceManager::loadSource));

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

    public AuthManagerProvider authManagerFactory() {
        return AuthManagerFactory::new;
    }

    public interface AuthManagerProvider {
        AuthManagerFactory provider(AuthConfig authConfig);

    }


    private ProtocolAdaptor protocolAdaptor(Integer businessQueueSize, Integer threadSize) {
        return Optional.ofNullable(ProtocolAdaptor.INSTANCE)
                .orElseGet(() -> new DefaultProtocolAdaptor(businessQueueSize, threadSize)).proxy();
    }

    private MetricManager metricManager(BootstrapConfig.MeterConfig meterConfig) {
        ConfigCheck.checkMeterConfig(meterConfig);
        return MetricManagerHolder.setMetricManager(Optional.ofNullable(MetricFactory.INSTANCE)
                .map(metricFactory -> metricFactory.initFactory(meterConfig).getMetricManager())
                .orElseGet(LocalMetricManager::new));

    }

    private AbstractConfiguration castConfiguration(T configuration) {
        return (AbstractConfiguration) configuration;
    }

    private IntegrateBuilder integrateBuilder() {
        return configuration -> new IgniteIntegrate(configuration, protocolAdaptor);
    }

    private IgniteConfiguration initConfig(BootstrapConfig.ClusterConfig clusterConfig) {
        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
        dataStorageConfiguration.setDataRegionConfigurations(getDataRegionConfigurations(IgniteCacheRegion.values()));
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);
        String localAddress= Optional.ofNullable(clusterConfig.getLocalAddress()).orElse(ServerUtils.serverIp);
        igniteConfiguration.setLocalHost(localAddress);
        igniteConfiguration.setConnectorConfiguration(new ConnectorConfiguration().setHost(localAddress));
        igniteConfiguration.setGridLogger(new Slf4jLogger());
        if(StringUtils.isNotEmpty(clusterConfig.getWorkDirectory())){
            igniteConfiguration.setWorkDirectory(clusterConfig.getWorkDirectory());
        }
        igniteConfiguration.setClientMode(false);
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        if(clusterConfig.getAddresses()!=null){
            // ip集群
            ipFinder.setAddresses(clusterConfig.getAddresses());
        }
        else{
            // 组播
            String multicastGroup = clusterConfig.getMulticastGroup();
            if(multicastGroup!=null){
                ipFinder.setMulticastGroup(multicastGroup);
            }
            Integer multicastPort = clusterConfig.getMulticastPort();
            if(multicastPort!=null){
                ipFinder.setMulticastPort(multicastPort);
            }
        }
        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        spi.setIpFinder(ipFinder);
        igniteConfiguration.setDiscoverySpi(spi);
        return igniteConfiguration;


    }

    private DataRegionConfiguration[] getDataRegionConfigurations(IgniteCacheRegion[] values) {
        DataRegionConfiguration[] regionConfigurations = new DataRegionConfiguration[values.length];
        for (int i = 0; i < values.length; i++) {
            regionConfigurations[i] = new DataRegionConfiguration()
                    .setName(values[i].getRegionName())
                    .setMaxSize(50000000)
                    .setPersistenceEnabled(values[i].persistence());
        }
        return regionConfigurations;
    }


}
