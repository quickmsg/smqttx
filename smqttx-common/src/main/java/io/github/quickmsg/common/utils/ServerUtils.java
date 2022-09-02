package io.github.quickmsg.common.utils;

import cn.hutool.core.net.NetUtil;

/**
 * @author luxurong
 */
public class ServerUtils {

    public static String serverIp;

    static {
        serverIp = NetUtil.getLocalhost().getHostAddress();
    }

}
