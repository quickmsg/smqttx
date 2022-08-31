package io.github.quickmsg.metric;

import io.github.quickmsg.common.metric.AbstractMetricRegistry;
import io.github.quickmsg.common.metric.MetricCounter;

import java.util.List;

/**
 * @author luxurong
 */
public class InfluxDbMetricRegistry extends AbstractMetricRegistry {


    public InfluxDbMetricRegistry(List<MetricCounter> counters) {
        super(counters);
    }
}
