package io.github.quickmsg.common.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

import java.io.*;

/**
 * @author luxurong
 */
@Slf4j
public class ClassPathLoader {


    public  static Mono<ByteBuf> readClassPathFile(String path) {
        try {
            InputStream inputStream = ClassPathLoader.class.getResourceAsStream(path);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int n ;
            while ((n = bufferedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, n);
            }
            return Mono.just(PooledByteBufAllocator.DEFAULT.directBuffer(out.size()).writeBytes(out.toByteArray()));
        } catch (IOException e) {
        }
        return Mono.empty();
    }


    public  static Mono<ByteBuf> readClassPathCompressFile(String path, HttpServerResponse response) {
        try {
            InputStream inputStream = ClassPathLoader.class.getResourceAsStream(path+".gz");
            if(inputStream ==null){
                inputStream = ClassPathLoader.class.getResourceAsStream(path);
            }
            else{
                response.header("Content-Encoding","gzip");
            }
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;
            while ((len = bufferedInputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return Mono.just(PooledByteBufAllocator.DEFAULT.directBuffer().writeBytes(bos.toByteArray()));
        } catch (IOException e) {
        }
        return Mono.empty();
    }

}
