package io.github.quickmsg;

import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.message.mqtt.PingMessage;
import org.apache.ignite.IgniteCache;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;

/**
 * @author luxurong
 * @date 2021/10/19 08:31
 * @description
 */
public class MapTest {
    static Map<String,Object> hashMap = new ConcurrentHashMap<>();
    public static void main(String[] args) {
        Sinks.Many<Message> acceptor = Sinks.many().multicast().onBackpressureBuffer();
        for(int i=0;i<4;i++){
//            parallel(10).runOn(Schedulers.parallel())
            acceptor.asFlux().publishOn(Schedulers.parallel()).subscribe(message -> {
                System.out.println(Thread.currentThread().getName()+message);
            });
            System.out.println("sdamdklamdma");
        }
        for(;;){
            try {
                Thread.sleep(2000);
                acceptor.tryEmitNext(PingMessage.INSTANCE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        hashMap.put("3","adsasdasdasd");
//        System.out.println(">> Executing the compute task");
//        long time1 = System.currentTimeMillis();
//        for (int i = 0; i < 500000; i++) {
//            System.out.println(">> " + hashMap.get("3"));
//        }
//
//        long time2 = System.currentTimeMillis();
//
//
//        System.out.println("cost time " + ((time2 - time1) / 1000) + "s");
    }
}
