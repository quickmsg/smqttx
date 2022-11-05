package io.github.quickmsg.metric;

import io.github.quickmsg.common.config.BootstrapConfig;
import io.github.quickmsg.common.metric.MetricFactory;
import io.github.quickmsg.common.metric.MetricManager;

/**
 * @author luxurong
 */
public class PrometheusMetricFactory implements MetricFactory {

    private  MetricManager metricManager;

    @Override
    public MetricFactory initFactory(BootstrapConfig.MeterConfig config) {
        metricManager = new PrometheusMetricManager(config);
        return this;
    }

    @Override
    public MetricManager getMetricManager() {
        return this.metricManager;
    }
}
