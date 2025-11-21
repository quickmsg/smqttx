package io.github.quickmsg.common.integrate.job;

import cn.hutool.core.util.ObjectUtil;

import java.io.Serializable;

/**
 * @author luxurong
 */
public interface JobClosure<INPUT,OUT> extends Serializable {
    /**
     * job名称
     *
     * @return String
     */
    String getJobName();
    
    /**
     * 应用函数
     * 
     * @param input 输入参数
     * @return 输出结果
     */
    OUT apply(INPUT input);
}