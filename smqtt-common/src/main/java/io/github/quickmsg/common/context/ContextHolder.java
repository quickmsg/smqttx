package io.github.quickmsg.common.context;

/**
 * @author luxurong
 */
public class ContextHolder {

    private static ReceiveContext<?> context;

    public static void setReceiveContext(ReceiveContext<?> context) {
        ContextHolder.context = context;
    }

    public static ReceiveContext<?> getReceiveContext() {
        return ContextHolder.context;
    }


}
