package io.github.quickmsg.interate.hazelcast;

import com.hazelcast.config.Config;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.IntegrateBuilder;
import io.github.quickmsg.common.protocol.ProtocolAdaptor;

/**
 * Hazelcast implementation of IntegrateBuilder
 *
 * @author hxx
 */
public class HazelcastIntegrateBuilder implements IntegrateBuilder {

    private final ProtocolAdaptor protocolAdaptor;

    public HazelcastIntegrateBuilder(ProtocolAdaptor protocolAdaptor) {
        this.protocolAdaptor = protocolAdaptor;
    }

    @Override
    public Integrate newIntegrate(Config configuration) {
        return new HazelcastIntegrate(configuration, protocolAdaptor);
    }
}