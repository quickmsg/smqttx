import ch.qos.logback.classic.Level;
import io.github.quickmsg.common.config.BootstrapConfig;
import io.github.quickmsg.core.Bootstrap;

/**
 * @author luxurong
 * @date 2021/5/6 19:25
 * @description
 */
public class ClusterNode3 {

    @org.junit.Test
    public void startServer() throws InterruptedException {

        Bootstrap bootstrap = Bootstrap.builder()
                .rootLevel(Level.DEBUG)
                .tcpConfig(
                        BootstrapConfig
                                .TcpConfig
                                .builder()
                                .port(8553)
                                .build())
                .clusterConfig(
                        BootstrapConfig.
                                ClusterConfig
                                .builder()
                                .enable(true).
                                build())
                .build()
                .start().block();
        assert bootstrap != null;
        // 关闭服
//        bootstrap.shutdown();
        Thread.sleep(1000000);
    }
}
