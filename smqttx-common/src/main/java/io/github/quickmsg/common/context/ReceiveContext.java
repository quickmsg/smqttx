package io.github.quickmsg.common.context;

import io.github.quickmsg.common.log.LogManager;
import io.github.quickmsg.common.retry.RetryManager;
import io.github.quickmsg.common.acl.AclManager;
import io.github.quickmsg.common.config.Configuration;
import io.github.quickmsg.common.handler.TrafficHandlerLoader;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.metric.MetricManager;
import io.github.quickmsg.common.protocol.ProtocolAdaptor;

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
     * get Log Manager
     * @return {@link  LogManager}
     */
    LogManager getLogManager();



    /**
     * check cluster
     *
     * @return  boolean
     */
    default boolean isCluster() {
       return getConfiguration().getClusterConfig().isEnable();
    }





}
