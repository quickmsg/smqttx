package io.github.quickmsg.common.event.message;

import io.github.quickmsg.common.event.Action;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.utils.JacksonUtil;

import java.util.Map;

/**
 * @author luxurong
 */
public abstract class MessageEvent implements Event {

    @SuppressWarnings("unchecked")
    public Map<String, Object> getCacheMap() {
        return (Map<String, Object>) JacksonUtil.json2List(JacksonUtil.bean2Json(this), Map.class);
    }

    @Override
    public Action getAction() {
        return Action.MESSAGE;
    }


}
