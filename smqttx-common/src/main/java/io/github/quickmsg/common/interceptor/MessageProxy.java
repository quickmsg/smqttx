package io.github.quickmsg.common.interceptor;

import io.github.quickmsg.common.protocol.ProtocolAdaptor;
import io.github.quickmsg.common.spi.loader.DynamicLoader;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
public class MessageProxy {

    private final List<Interceptor> interceptors = DynamicLoader.findAll(Interceptor.class)
            .sorted(Comparator.comparing(Interceptor::sort))
            .collect(Collectors.toList());

    public ProtocolAdaptor proxy(ProtocolAdaptor protocolAdaptor) {
        for (Interceptor interceptor : interceptors) {
            protocolAdaptor = interceptor.proxyProtocol(protocolAdaptor);
        }
        return protocolAdaptor;
    }


}
