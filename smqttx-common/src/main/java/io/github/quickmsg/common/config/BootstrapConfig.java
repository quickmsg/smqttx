package io.github.quickmsg.common.config;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.quickmsg.common.metric.MeterType;
import io.github.quickmsg.common.rule.RuleChainDefinition;
import io.github.quickmsg.common.rule.source.SourceDefinition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author luxurong
 */
@Data
public class BootstrapConfig {

    @JsonProperty("smqtt")
    private SmqttConfig smqttConfig;

    public static BootstrapConfig defaultConfig() {
        BootstrapConfig bootstrapConfig = new BootstrapConfig();
        SmqttConfig smqttConfig = new SmqttConfig();
        TcpConfig tcpConfig = new TcpConfig();
        tcpConfig.setPort(1883);
        smqttConfig.setTcpConfig(tcpConfig);
        smqttConfig.setLogLevel("INFO");
        bootstrapConfig.setSmqttConfig(smqttConfig);
        smqttConfig.setClusterConfig(ClusterConfig.builder()
                    .enable(false).build());
        smqttConfig.setHttpConfig(HttpConfig.builder().host("0.0.0.0").build());
        smqttConfig.setWebsocketConfig(WebsocketConfig.builder()
                    .enable(false).build());
        return bootstrapConfig;
    }


    @Data
    public static class SmqttConfig {

        /**
         * sfl4j日志级别
         *
         * @see Level
         */
        private String logLevel;

        /**
         * tcp配置
         */
        @JsonProperty("tcp")
        private TcpConfig tcpConfig;

        /**
         * http配置
         */
        @JsonProperty("http")
        private HttpConfig httpConfig;

        /**
         * websocket配置
         */
        @JsonProperty("ws")
        private WebsocketConfig websocketConfig;

        /**
         * 集群配置配置
         */
        @JsonProperty("cluster")
        private ClusterConfig clusterConfig;

        /**
         * 规则定义
         */
        @JsonProperty("rules")
        private List<RuleChainDefinition> ruleChainDefinitions;

        /**
         * 规则定义
         */
        @JsonProperty("sources")
        private List<SourceDefinition> ruleSources;

        /**
         * 指标配置
         */
        @JsonProperty("meter")
        private MeterConfig meterConfig;

        /**
         * acl
         */
        @JsonProperty("auth")
        private AuthConfig authConfig;


    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TcpConfig {
        /**
         * 端口
         */
        private Integer port;

        /**
         * 二进制日志（需要开启root 为debug）
         */
        private Boolean wiretap;
        /**
         * 核心线程数
         */
        private Integer bossThreadSize;
        /**
         * 工作线程数
         */
        private Integer workThreadSize;

        /**
         * 业务线程数
         */
        private Integer businessThreadSize;

        /**
         * 业务队列
         */
        private Integer businessQueueSize;

        /**
         * 接收消息的最大限制
         */
        private Integer messageMaxSize;

        /**
         * 低水位
         */
        private Integer lowWaterMark;

        /**
         * 高水位
         */
        private Integer highWaterMark;
        /**
         * ssl配置
         */
        private SslContext ssl;

        /**
         * 全局写字节限制
         */
        private String globalReadWriteSize;

        /**
         * 单个连接读写字节限制
         */
        private String channelReadWriteSize;


        /**
         * server channel options
         */
        Map<String, Object> options;

        /**
         * child client channel options
         */
        Map<String, Object> childOptions;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HttpConfig {
        /**
         * ip
         */
        private String host;
        /**
         * port
         */
        private Integer port;
        /**
         * http日志
         */
        private boolean accessLog;
        /**
         * ssl配置
         */
        private SslContext ssl;
        /**
         * 管理页面配置
         */
        private HttpAdmin admin;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WebsocketConfig {
        /**
         * 端口
         */
        private Integer port;
        /**
         * websocket path
         * mqtt.js 需要设置 /mqtt
         */
        private String path;
        /**
         * 开启ws
         */
        private boolean enable;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClusterConfig {
        /**
         * 开启集群
         */
        private boolean enable;

        /**
         * 本地ip
         */
        private String localAddress;


        /**
         * 集群ip集合
         */
        private List<String> addresses;

        /**
         * 组播ip
         */
        private String multicastGroup;


        /**
         * 组播端口
         */
        private Integer multicastPort;

        /**
         * 集群持久化文件
         */
        private String workDirectory;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HttpAdmin {
        /**
         * 用户名
         */
        private String username;
        /**
         * 密码
         */
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClusterExternal {
        /**
         * 本地曝光host
         */
        private String host;

        /**
         * 本地曝光port
         */
        private Integer port;
    }

    /**
     * 指标配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MeterConfig {

        private MeterType meterType;

        private Influxdb influxdb;

    }

    /**
     * influx1.x参数
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Influxdb {
        /**
         * 数据库
         */
        private String db;
        /**
         * uri
         */
        private String uri;
        /**
         * 用户名
         */
        private String userName;
        /**
         * 密码
         */
        private String password;
        /**
         * 步长
         */
        private int step;
    }
}
