package io.github.quickmsg.source.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.quickmsg.common.rule.source.Source;
import io.github.quickmsg.common.rule.source.SourceBean;
import io.github.quickmsg.source.db.config.HikariCPConnectionProvider;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;


/**
 * 数据源
 *
 * @author zhaopeng
 */
@Slf4j
public class DbSourceBean implements SourceBean {


    @Override
    public Boolean support(Source source) {
        return source == Source.DATA_BASE;
    }


    private  HikariDataSource hikariDataSource;


    /**
     * 初始化数据库连接
     *
     * @param sourceParam 参数
     * @return Boolean
     */
    @Override
    public Boolean bootstrap(Map<String, Object> sourceParam) {
        Properties properties = new Properties();
        for (String key : sourceParam.keySet()) {
            properties.put(key.replaceAll("-", "."), sourceParam.get(key).toString());
        }

        try {
            HikariConfig config = new HikariConfig(properties);
            this.hikariDataSource = new HikariDataSource(config);;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 转发数据
     *
     * @param object 对象
     */
    @Override
    public void transmit(Object object) {
        try (Connection connection = hikariDataSource.getConnection()) {
            DSLContext dslContext = DSL.using(connection);
            dslContext.execute(object.toString());
        } catch (Exception e) {
            log.error("execute sql error", e);
        }
    }


    @Override
    public void close() {
        hikariDataSource.close();
    }

}
