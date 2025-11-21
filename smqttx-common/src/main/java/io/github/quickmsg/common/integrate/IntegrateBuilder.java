package io.github.quickmsg.common.integrate;


import com.hazelcast.config.Config;

/**
 * @author luxurong
 */
public interface IntegrateBuilder {

    Integrate newIntegrate(Config configuration);

}
