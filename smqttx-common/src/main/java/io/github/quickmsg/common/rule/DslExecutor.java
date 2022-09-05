package io.github.quickmsg.common.rule;

import io.github.quickmsg.common.message.Message;

/**
 * @author luxurong
 */
public interface DslExecutor {

    /**
     * 执行
     * @param message {@link  Message}
=     */
    void executeRule(Message message);


    /**
     * 执行
     * @return  boolean 是否需要执行
     */
    Boolean isExecute();

}
