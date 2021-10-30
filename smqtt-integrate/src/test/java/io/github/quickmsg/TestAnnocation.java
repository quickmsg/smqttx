package io.github.quickmsg;

import io.github.quickmsg.common.interate1.job.JobFor;

/**
 * @author luxurong
 * @date 2021/10/29 08:02
 * @description
 */
public class TestAnnocation {

    @JobFor(name = "job")
    public int tet() {
        System.out.println("我被调用了");
        return 1;
    }
}
