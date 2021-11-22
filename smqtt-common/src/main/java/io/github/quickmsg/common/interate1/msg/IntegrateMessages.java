package io.github.quickmsg.common.interate1.msg;

import io.github.quickmsg.common.interate1.IntegrateGetter;
import io.github.quickmsg.common.message.RetainMessage;
import io.github.quickmsg.common.message.SessionMessage;

import java.util.List;

/**
 * @author luxurong
 */
public interface IntegrateMessages extends IntegrateGetter {

    List<SessionMessage> getSessionMessage(String clientIdentifier);

    void saveRetainMessage(RetainMessage of);

    void saveSessionMessage(SessionMessage of);

    Iterable<RetainMessage> getRetainMessage(String topicName);

}
