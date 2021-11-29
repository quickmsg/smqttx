package io.github.quickmsg.common.integrate;

import org.apache.ignite.configuration.IgniteConfiguration;

/**
 * @author luxurong
 */
public interface IntegrateBuilder {

    Integrate newIntegrate(IgniteConfiguration configuration);

}
