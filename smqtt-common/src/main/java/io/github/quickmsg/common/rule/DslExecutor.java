package io.github.quickmsg.common.rule;

import io.github.quickmsg.common.event.Event;

/**
 * @author luxurong
 */
public interface DslExecutor {

    /**
     * 执行
     * @param event {@link  Event}
=     */
    void executeRule(Event event);


    /**
     * 执行
     * @return  boolean 是否需要执行
     */
    Boolean isExecute();

}
