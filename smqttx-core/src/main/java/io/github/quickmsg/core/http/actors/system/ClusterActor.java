package io.github.quickmsg.core.http.actors.system;

import io.github.quickmsg.common.config.Configuration;
import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.http.HttpActor;
import io.github.quickmsg.common.http.annotation.AllowCors;
import io.github.quickmsg.common.http.annotation.Header;
import io.github.quickmsg.common.http.annotation.Router;
import io.github.quickmsg.common.http.enums.HttpType;
import io.github.quickmsg.common.integrate.job.JobCaller;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.github.quickmsg.core.http.HttpConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.cluster.ClusterNode;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.Collection;

/**
 * @author luxurong
 */
@Router(value = "/smqtt/cluster", type = HttpType.GET)
@Slf4j
@Header(key = "Content-Type", value = "application/json")
@AllowCors
public class ClusterActor implements HttpActor {


    @Override
    public Publisher<Void> doRequest(HttpServerRequest request, HttpServerResponse response, Configuration httpConfiguration) {
        HttpConfiguration configuration = (HttpConfiguration) httpConfiguration;
        return request
                    .receive()
                    .then(response.sendString(Mono.just(JacksonUtil.bean2Json(getClusterInfo()))).then());
    }


    public Collection<ClusterInfo> getClusterInfo() {
        return ContextHolder.getReceiveContext()
                    .getIntegrate().getJobExecutor()
                    .callBroadcast(new JobCaller<ClusterInfo>() {
                        @Override
                        public ClusterInfo call() throws Exception {
                            ClusterNode clusterNode = ContextHolder.getReceiveContext()
                                        .getIntegrate().getIgnite().cluster().localNode();
                            ClusterInfo clusterInfo = new ClusterInfo();
                            clusterInfo.setClusterId(clusterNode.consistentId().toString());
                            clusterInfo.setNodeIp(clusterNode.addresses().stream().findAny().orElse(null));
                            clusterInfo.setHttpUrl(ContextHolder.getHttpUrl());
                            return clusterInfo;
                        }

                        @Override
                        public String getJobName() {
                            return null;
                        }
                    });
    }

    @Data
    public static class ClusterInfo {

        private String clusterId;

        private String httpUrl;

        private String nodeIp;

    }
}

