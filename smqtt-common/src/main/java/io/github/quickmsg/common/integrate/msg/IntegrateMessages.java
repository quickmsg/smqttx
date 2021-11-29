package io.github.quickmsg.common.integrate.msg;

import io.github.quickmsg.common.integrate.IntegrateGetter;
import io.github.quickmsg.common.message.RetainMessage;
import io.github.quickmsg.common.message.SessionMessage;

import java.util.List;
import java.util.Set;

/**
 * @author luxurong
 */
public interface IntegrateMessages extends IntegrateGetter {

    Set<SessionMessage> getSessionMessage(String clientIdentifier);

    void deleteSessionMessage(String clientIdentifier);

    void saveSessionMessage(SessionMessage of);

    void saveRetainMessage(RetainMessage of);

    void deleteRetainMessage(String clientIdentifier);

    RetainMessage getRetainMessage(String topicName);

}
