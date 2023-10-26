package io.github.quickmsg.solon.config;

import ch.qos.logback.classic.Level;
import io.github.quickmsg.common.utils.IPUtils;
import io.github.quickmsg.core.Bootstrap;
import io.github.quickmsg.solon.SolonBootstrapConfig;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

/**
 * @author luxurong
 */
@Slf4j
@Configuration
public class AutoMqttConfiguration {


    /**
     * 配置异常切面
     *
     * @param solonBootstrapConfig {@link SolonBootstrapConfig}
     * @return {@link Bootstrap}
     */
    @Bean
    public Bootstrap startServer(SolonBootstrapConfig solonBootstrapConfig) {
        return Bootstrap.builder()
                .rootLevel(Level.toLevel(solonBootstrapConfig.getLogLevel()))
                .tcpConfig(solonBootstrapConfig.getTcp())
                .httpConfig(solonBootstrapConfig.getHttp())
                .websocketConfig(solonBootstrapConfig.getWs())
                .clusterConfig(solonBootstrapConfig.getCluster())
                .ruleChainDefinitions(solonBootstrapConfig.getRules())
                .sourceDefinitions(solonBootstrapConfig.getSources())
                .meterConfig(solonBootstrapConfig.getMeter())
                .authConfig(solonBootstrapConfig.getAuth())
                .meterConfig(solonBootstrapConfig.getMeter())
                .build()
                .start()
                .doOnSuccess(this::printUiUrl).block();
    }

    public void printUiUrl(Bootstrap bootstrap) {
        String start = "\n-------------------------------------------------------------\n\t";
        start += String.format("Smqtt mqtt connect url %s:%s \n\t", IPUtils.getIP(), bootstrap.getTcpConfig().getPort());
        if (bootstrap.getHttpConfig() != null ) {
            Integer port = 60000;
            start += String.format("Smqtt-Admin UI is running AccessURLs:\n\t" +
                    "Http Local url:    http://localhost:%s/smqtt/admin" + "\n\t" +
                    "Http External url: http://%s:%s/smqtt/admin" + "\n" +
                    "-------------------------------------------------------------", port, IPUtils.getIP(), port);
        }
        log.info(start);
    }
}
