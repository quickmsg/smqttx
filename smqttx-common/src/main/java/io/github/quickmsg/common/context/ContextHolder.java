package io.github.quickmsg.common.context;

import io.github.quickmsg.common.channel.MqttChannel;

/**
 * @author luxurong
 */
public class ContextHolder {

    private static ReceiveContext<?> context;

    private static String httpUrl;

    public static void setReceiveContext(ReceiveContext<?> context) {
        ContextHolder.context = context;
    }


    public static void setHttpUrl(String  httpUrl) {
        ContextHolder.httpUrl = httpUrl;
    }

    public static ReceiveContext<?> getReceiveContext() {
        return ContextHolder.context;
    }

    public static String getHttpUrl() {
        return ContextHolder.httpUrl;
    }

}
