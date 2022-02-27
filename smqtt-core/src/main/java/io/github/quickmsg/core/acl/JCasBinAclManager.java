package io.github.quickmsg.core.acl;

import io.github.quickmsg.common.acl.AclAction;
import io.github.quickmsg.common.acl.AclManager;
import io.github.quickmsg.common.acl.AclPolicy;
import io.github.quickmsg.common.config.AclConfig;
import lombok.extern.slf4j.Slf4j;
import org.casbin.adapter.JDBCAdapter;
import org.casbin.jcasbin.main.Enforcer;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author luxurong
 */
@Slf4j
public class JCasBinAclManager implements AclManager {

    private Enforcer enforcer;

    public JCasBinAclManager(AclConfig aclConfig) {
        String rootPath = Optional.ofNullable(Thread.currentThread().getContextClassLoader().getResource("")).map(URL::getPath).orElse(null);
        if (aclConfig.getAclPolicy() == AclPolicy.JDBC) {
            AclConfig.JdbcAclConfig jdbcAclConfig = aclConfig.getJdbcAclConfig();
            Objects.requireNonNull(jdbcAclConfig);
            try {
                enforcer = new Enforcer(rootPath + "mqtt_model.conf", new JDBCAdapter(jdbcAclConfig.getDriver(), jdbcAclConfig.getUrl(), jdbcAclConfig.getUsername(), jdbcAclConfig.getPassword()));
            } catch (Exception e) {
                log.error("init acl jdbc error {}", aclConfig, e);
            }
        } else  if (aclConfig.getAclPolicy() == AclPolicy.File){
            enforcer = new Enforcer(rootPath + "mqtt_model.conf", aclConfig.getFilePath());
        }
    }

    @Override
    public boolean auth(String sub, String source, AclAction action) {
        return  Optional.ofNullable(enforcer)
                .map(ef->enforcer.enforce(sub,source,action.name()))
                .orElse(true);
    }

    @Override
    public boolean add(String sub, String source, AclAction action) {
        return  Optional.ofNullable(enforcer)
                .map(ef->enforcer.addNamedPolicy("p",sub,source,action.name()))
                .orElse(true);

    }

    @Override
    public boolean delete(String sub, String source, AclAction action) {
        return  Optional.ofNullable(enforcer)
                .map(ef->enforcer.addNamedPolicy("p",sub,source,action.name()))
                .orElse(true);
    }

    @Override
    public List<List<String>> get() {
        return  Optional.ofNullable(enforcer)
                .map(ef->enforcer.getNamedPolicy("p"))
                .orElse(Collections.emptyList());
    }
}
