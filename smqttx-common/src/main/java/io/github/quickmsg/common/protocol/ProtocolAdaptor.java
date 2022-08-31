package io.github.quickmsg.common.protocol;

import io.github.quickmsg.common.interceptor.Intercept;
import io.github.quickmsg.common.interceptor.MessageProxy;
import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.spi.loader.DynamicLoader;
import reactor.core.publisher.Sinks;

/**
 * @author luxurong
 */
public interface ProtocolAdaptor {

    ProtocolAdaptor INSTANCE = DynamicLoader.findFirst(ProtocolAdaptor.class).orElse(null);



    MessageProxy MESSAGE_PROXY = new MessageProxy();

    /**
     * choose protocol
     *
     * @param message {@link Message}
     */
    @Intercept
    void chooseProtocol(Message message);


    /**
     * proxy
     *
     * @return {@link ProtocolAdaptor}
     */
    default ProtocolAdaptor proxy() {
        return MESSAGE_PROXY.proxy(this);
    }


}
