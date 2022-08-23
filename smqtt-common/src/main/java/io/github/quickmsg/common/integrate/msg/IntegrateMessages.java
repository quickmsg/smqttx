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

    void saveRetainMessage(RetainMessage of);

    void deleteRetainMessage(String clientIdentifier);

    Set<RetainMessage> getRetainMessage(String topicName);

}
