package io.github.quickmsg.core.http.actors.resource;

import io.github.quickmsg.common.http.annotation.AllowCors;
import io.github.quickmsg.common.http.annotation.Router;
import io.github.quickmsg.common.config.Configuration;
import io.github.quickmsg.common.http.enums.HttpType;
import io.github.quickmsg.common.http.HttpActor;
import io.github.quickmsg.common.utils.ClassPathLoader;
import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

/**
 * @author luxurong
 */
@Router(type = HttpType.GET, value = "/static/**",resource = true)
@AllowCors
public class StaticResourceActor implements HttpActor {

    @Override
    public Publisher<Void> doRequest(HttpServerRequest request, HttpServerResponse response, Configuration httpConfiguration) {
        String path = "/static/"+request.path();
        if(path.endsWith(".css") || path.endsWith(".js") ){
            return response.send(ClassPathLoader.readClassPathCompressFile(path,response)).then();
        }
        else{
            if(path.endsWith(".svg")){
                // 设置响应头
                response.header("Content-Type", "image/svg+xml");
                response.header("Cache-Control", "no-cache, no-store, must-revalidate");
                response.header("Pragma", "no-cache");
                response.header("Expires", "0");
            }
            return response.send(ClassPathLoader.readClassPathFile(path)).then();
        }
    }
}
