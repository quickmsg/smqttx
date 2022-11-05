package io.github.quickmsg.common.metric;


import io.github.quickmsg.common.config.BootstrapConfig;
import io.github.quickmsg.common.spi.loader.DynamicLoader;

/**
 * @author luxurong
 */
public interface MetricFactory {

    MetricFactory INSTANCE = DynamicLoader.findFirst(MetricFactory.class).orElse(null);


    MetricFactory initFactory(BootstrapConfig.MeterConfig config);

    MetricManager getMetricManager();


}
