package io.github.quickmsg.common.disruptor;

import com.lmax.disruptor.WorkHandler;
import io.github.quickmsg.common.message.Message;

/**
 * @author luxurong
 */
public class MessageHandler  implements WorkHandler<Message> {


    @Override
    public void onEvent(Message message) throws Exception {

    }


}
