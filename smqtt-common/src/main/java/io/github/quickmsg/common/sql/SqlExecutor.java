package io.github.quickmsg.common.sql;

import java.util.Map;

/**
 * @author luxurong
 */
public interface SqlExecutor {

    Map<String,Object> execute(String sqlScript);

}
