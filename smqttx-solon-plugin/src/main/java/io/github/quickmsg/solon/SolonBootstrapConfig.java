package io.github.quickmsg.solon;

import ch.qos.logback.classic.Level;
import io.github.quickmsg.common.config.AuthConfig;
import io.github.quickmsg.common.config.BootstrapConfig;
import io.github.quickmsg.common.rule.RuleChainDefinition;
import io.github.quickmsg.common.rule.source.SourceDefinition;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.List;

/**
 * @author luxurong
 * @since noear
 */
@Getter
@Setter
@ToString
@Inject("${smqtt}")
@Configuration
public class SolonBootstrapConfig {

    /**
     * sfl4j日志级别
     *
     * @see Level
     */
    private String logLevel;

    /**
     * tcp配置
     */
    private BootstrapConfig.TcpConfig tcp;

    /**
     * http配置
     */
    private BootstrapConfig.HttpConfig http;

    /**
     * websocket配置
     */
    private BootstrapConfig.WebsocketConfig ws;

    /**
     * 集群配置配置
     */
    private BootstrapConfig.ClusterConfig cluster;

    /**
     * meter配置
     */
    private BootstrapConfig.MeterConfig meter;

    /**
     * 规则定义
     */
    private List<RuleChainDefinition> rules;

    /**
     * 规则定义
     */
    private List<SourceDefinition> sources;

    /**
     * auth
     */
    private AuthConfig auth;

}
