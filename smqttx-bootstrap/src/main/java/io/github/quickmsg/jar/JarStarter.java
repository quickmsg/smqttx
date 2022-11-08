package io.github.quickmsg.jar;

import cn.hutool.db.sql.Condition;
import cn.hutool.db.sql.LogicalOperator;
import cn.hutool.db.sql.SqlBuilder;
import io.github.quickmsg.AbstractStarter;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.message.mqtt.CloseMessage;
import io.github.quickmsg.common.utils.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.jooq.meta.derby.sys.Sys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luxurong
 */
@Slf4j
public class JarStarter extends AbstractStarter {

    public static void main(String[] args) throws IOException {
        log.info("JarStarter start args {}", String.join(",", args));
        if (args.length > 0) {
            start(args[0]);
        } else {
            start(null);
        }
    }
}
