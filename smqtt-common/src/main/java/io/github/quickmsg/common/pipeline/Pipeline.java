package io.github.quickmsg.common.pipeline;

import io.github.quickmsg.common.message.HeapMqttMessage;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author luxurong
 */
public interface Pipeline<T> extends Consumer<T> {




}
