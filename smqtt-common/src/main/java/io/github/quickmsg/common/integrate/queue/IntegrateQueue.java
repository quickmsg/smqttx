package io.github.quickmsg.common.integrate.queue;

import io.github.quickmsg.common.event.Event;

import java.util.Queue;

/**
 * @author luxurong
 */
public interface IntegrateQueue extends Queue<Event>, Runnable {


}
