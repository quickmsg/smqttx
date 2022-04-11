package io.github.quickmsg.common.context;

import io.github.quickmsg.common.ack.RetryManager;
import io.github.quickmsg.common.acl.AclManager;
import io.github.quickmsg.common.config.Configuration;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.NoneEvent;
import io.github.quickmsg.common.handler.TrafficHandlerLoader;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.metric.MetricManager;
import io.github.quickmsg.common.protocol.ProtocolAdaptor;
import io.github.quickmsg.common.rule.RuleDslAcceptor;
import io.github.quickmsg.common.spi.registry.EventRegistry;

import java.util.function.Consumer;

/**
 * @author luxurong
 */

public interface ReceiveContext<T extends Configuration> extends Consumer<Message> {


    /**
     * 协议转换器
     *
     * @return {@link ProtocolAdaptor}
     */
    ProtocolAdaptor getProtocolAdaptor();


    /**
     * 消息感知/设备感知
     *
     * @return {@link EventRegistry}
     */
    EventRegistry getEventRegistry();


    /**
     * 规则引擎注册器
     *
     * @return {@link RuleDslAcceptor}
     */
    RuleDslAcceptor getRuleDslAcceptor();


    /**
     * 获取配置文件
     *
     * @return {@link Configuration}
     */
    T getConfiguration();


    MetricManager getMetricManager();


    /**
     * 全局流控
     *
     * @return {@link TrafficHandlerLoader}
     */
    TrafficHandlerLoader getTrafficHandlerLoader();


    /**
     * get Integrate
     *
     * @return {@link Integrate }
     */
    Integrate getIntegrate();

    /**
     * get retry
     *
     * @return {@link RetryManager }
     */
    RetryManager getRetryManager();


    /**
     * get acl
     *
     * @return {@link  AclManager}
     */
    AclManager getAclManager();



    /**
     * submit event pipeline
     *
     * @param event {@link Event }
     */
    default void submitEvent(Event event) {
        if (!(event instanceof NoneEvent)) {
            getIntegrate().getPipeline().accept(event);
        }
    }

    /**
     * check cluster
     *
     * @return  boolean
     */
    default boolean isCluster() {
       return getConfiguration().getClusterConfig().isEnable();
    }





}
