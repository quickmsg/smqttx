package io.github.quickmsg.common.log;

import cn.hutool.core.collection.ConcurrentHashSet;
import io.github.quickmsg.common.channel.MqttChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @author luxurong
 */
@Slf4j
public class LogManager {
    public volatile static boolean logAll = true;

    private final ConcurrentHashSet<String> debugClientIds = new ConcurrentHashSet<>();

    /**
     * 节点IP
     */
    private final String nodeIp;

    public LogManager(String nodeIp) {
        this.nodeIp = nodeIp;
    }

    public void addDebugClient(String client) {
        debugClientIds.add(client);
    }

    public void removeDebugClient(String client) {
        debugClientIds.remove(client);
    }

    public void printInfo(MqttChannel mqttChannel, LogEvent type, LogStatus eventStatus, String message) {
        if (logAll) {
            log.info("{}|{}|{}|{}|{}|{}",
                    nodeIp,
                    Optional.ofNullable(mqttChannel).map(MqttChannel::getAddress).orElse(null),
                    Optional.ofNullable(mqttChannel).map(MqttChannel::getClientId).orElse(null),
                    type.getName(),
                    eventStatus.getName(),
                    message);
        }
    }

    /**
     * error日志打印
     */
    public void printError(MqttChannel mqttChannel, LogEvent type, String message) {
        if (logAll) {
            log.info("{}|{}|{}|{}|{}|{}",
                    nodeIp,
                    Optional.ofNullable(mqttChannel).map(MqttChannel::getAddress).orElse("system"),
                    Optional.ofNullable(mqttChannel).map(MqttChannel::getClientId).orElse("system"),
                    type.getName(),
                    LogStatus.FAILED.getName(),
                    message);
        }
    }



    /**
     * warn日志打印
     */
    public void printWarn(MqttChannel mqttChannel, LogEvent type, LogStatus logStatus,String message) {
        if (logAll){
            log.info("{}|{}|{}|{}|{}|{}",
                    nodeIp,
                    Optional.ofNullable(mqttChannel).map(MqttChannel::getAddress).orElse(null),
                    Optional.ofNullable(mqttChannel).map(MqttChannel::getClientId).orElse(null),
                    type.getName(),
                    logStatus.getName(),
                    message);
        }
    }


}
