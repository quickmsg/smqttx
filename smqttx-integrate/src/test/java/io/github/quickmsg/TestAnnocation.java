package io.github.quickmsg;

import io.github.quickmsg.common.integrate.job.JobFor;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author luxurong
 * @date 2021/10/29 08:02
 * @description
 */
public class TestAnnocation {

//    @JobFor(name = "job")
    @Test
    public void tet() throws InterruptedException {
        final Mono<String> mono = Mono.just("hello "); // Mono<String>在主线程中完成装配
        mono.subscribeOn(Schedulers.parallel()).subscribe(s->{
            System.out.println(s + Thread.currentThread().getName()) ;// 结果，map和filter都在新的线程执行

        })  ;
        Thread t = new Thread(() -> mono
                .map(msg -> msg + "thread ")
                .subscribe(v -> // 在新线程中完成订阅
                        System.out.println(v + Thread.currentThread().getName()) // 结果，map和filter都在新的线程执行
                )
        );
        t.start();
        t.join();
    }
}
