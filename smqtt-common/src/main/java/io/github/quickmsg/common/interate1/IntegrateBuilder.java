package io.github.quickmsg.common.interate1;

import io.github.quickmsg.common.config.Configuration;
import org.apache.ignite.configuration.IgniteConfiguration;

/**
 * @author luxurong
 */
public interface IntegrateBuilder {

    Integrate newIntegrate(IgniteConfiguration configuration);

}
