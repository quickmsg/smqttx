package io.github.quickmsg.common.integrate.job;

import cn.hutool.core.util.ObjectUtil;
import org.apache.ignite.lang.IgniteClosure;

/**
 * @author luxurong
 */
public interface JobClosure<INPUT,OUT> extends IgniteClosure<INPUT, OUT>{

    /**
     * job名称
     *
     * @return String
     */
    String getJobName();

}
