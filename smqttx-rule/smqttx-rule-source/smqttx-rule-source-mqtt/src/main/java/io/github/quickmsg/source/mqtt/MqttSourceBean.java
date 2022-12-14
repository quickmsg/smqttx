package io.github.quickmsg.source.mqtt;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.connect.Mqtt3ConnectBuilder;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import io.github.quickmsg.common.rule.source.Source;
import io.github.quickmsg.common.rule.source.SourceBean;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


/**
 * mqtt
 *
 * @author zhaopeng
 */
@Slf4j
public class MqttSourceBean implements SourceBean {

    private Mqtt3AsyncClient client;

    @Override
    public Boolean support(Source source) {
        return source == Source.MQTT;
    }

    /**
     * 初始化
     *
     * @param sourceParam 参数
     * @return boolean
     */
    @Override
    public Boolean bootstrap(Map<String, Object> sourceParam) {
        try {
            String clientId = sourceParam.get("clientId").toString();
            String host = sourceParam.get("host").toString();
            Integer port = Integer.parseInt(sourceParam.get("port").toString());

            client = MqttClient.builder()
                    .useMqttVersion3()
                    .identifier(clientId)
                    .serverHost(host)
                    .serverPort(port)
                    .buildAsync();

            Mqtt3ConnectBuilder.Send<CompletableFuture<Mqtt3ConnAck>> completableFutureSend = client.connectWith();

            if (sourceParam.get("userName") != null && sourceParam.get("passWord") != null) {
                String userName = sourceParam.get("userName").toString();
                String passWord = sourceParam.get("passWord").toString();
                if (!StringUtil.isNullOrEmpty(userName) && !StringUtil.isNullOrEmpty(passWord)) {
                    completableFutureSend.simpleAuth()
                            .username(userName)
                            .password(passWord.getBytes())
                            .applySimpleAuth();
                }

            }

            completableFutureSend
                    .send()
                    .whenComplete((connAck, throwable) -> {
                        if (throwable != null) {
                            // handle failure
                            log.error("mqtt client connect error", throwable);
                        }
                    });

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 转发数据
     *
     * @param param 对象
     */
    @Override
    public void transmit(Object param) {
        Map<String, Object> object = (Map<String, Object> )param;
        String topic = (String) object.get("topic");
        if (topic == null) {
            log.error("MqttSourceBean transmit topic is not null");
            return;
        }
        byte[] bytes = Optional.ofNullable(object.get("msg")).map(msg -> msg.toString().getBytes(StandardCharsets.UTF_8)).orElseGet(() -> new byte[0]);
        boolean retain = Optional.ofNullable(object.get("retain")).map(r -> Boolean.getBoolean(r.toString())).orElse(false);
        Integer qos = Optional.ofNullable((Integer) object.get("qos")).orElse(0);
        client.publishWith()
                .topic(topic)
                .payload(bytes)
                .qos(Objects.requireNonNull(MqttQos.fromCode(qos)))
                .retain(retain)
                .send()
                .whenComplete((publish, throwable) -> {
                    if (throwable != null) {
                        // handle failure to publish
                        log.error("mqtt client publish error", throwable);
                    }
                });
    }


    @Override
    public void close() {
        if (client != null) {
            client.disconnect();
        }
    }
}
