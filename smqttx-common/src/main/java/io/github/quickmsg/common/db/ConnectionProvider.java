package io.github.quickmsg.common.db;

import java.sql.Connection;
import java.util.Properties;


/**
 * 数据库连接提供者
 *
 * @author easy
 */
public interface ConnectionProvider {


    /**
     * 初始化
     *
     * @param properties 配置
     */
    void init(Properties properties);

    /**
     * 获取链接
     *
     * @return {@link Connection}
     */
    Connection getConnection();


    /**
     * 关闭链接
     */
    void shutdown();


}
