package io.github.quickmsg.core.acl;

import io.github.quickmsg.common.acl.AclAction;
import io.github.quickmsg.common.acl.AclManager;
import io.github.quickmsg.common.acl.AclPolicy;
import io.github.quickmsg.common.acl.AclType;
import io.github.quickmsg.common.acl.filter.AclFunction;
import io.github.quickmsg.common.acl.model.PolicyModel;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.config.AclConfig;
import io.github.quickmsg.common.integrate.cache.IntegrateCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteSet;
import org.casbin.adapter.JDBCAdapter;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.file_adapter.FileAdapter;
import org.casbin.jcasbin.util.BuiltInFunctions;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luxurong
 */
@Slf4j
public class JCasBinAclManager implements AclManager {

    private Enforcer enforcer;


    private final String REQUEST_SUBJECT_TEMPLATE = "%s:%s";


    public JCasBinAclManager(IntegrateCache<String, Object> cache) {
        Model model = new Model();
        model.addDef("r", "r", "sub, obj, act");
        model.addDef("p", "p", " sub, obj, act, eft");
        model.addDef("g", "g", "_, _");
        model.addDef("e", "e", "some(where (p.eft == allow)) && !some(where (p.eft == deny))");
        model.addDef("m", "m", "r.act == p.act && keyMatch(r.obj,p.obj)  && filter(r.sub, p.sub)");
        enforcer = new Enforcer(model, new IgniteAdaptor(cache));
        enforcer.addFunction("filter", new AclFunction());
        this.loadAclCache();
    }


    private void loadAclCache() {
        this.add("all", "*", AclAction.PUBLISH, AclType.ALLOW);
        this.add("all", "*", AclAction.SUBSCRIBE, AclType.ALLOW);
    }

    @Override
    public boolean check(MqttChannel mqttChannel, String source, AclAction action) {
        try {
            String subject = String.format(REQUEST_SUBJECT_TEMPLATE, mqttChannel.getClientId()
                        , mqttChannel.getAddress().split(":")[0]);
            return Optional.ofNullable(enforcer)
                        .map(ef -> ef.enforce(subject, source, action.name()))
                        .orElse(true);
        } catch (Exception e) {
            log.error("acl check error", e);
        }
        return true;
    }

    @Override
    public boolean add(String sub, String source, AclAction action, AclType type) {
        return Optional.ofNullable(enforcer)
                    .map(ef -> enforcer.addNamedPolicy("p", sub, source, action.name(), type.getDesc()))
                    .orElse(true);
    }

    @Override
    public boolean delete(String sub, String source, AclAction action, AclType type) {
        return  Optional.ofNullable(enforcer)
                    .map(ef -> enforcer.removeNamedPolicy("p", sub, source, action.name(), type.getDesc()))
                    .orElse(true);
    }

    @Override
    public List<List<String>> get(PolicyModel policyModel) {
        return Optional.ofNullable(enforcer)
                    .map(ef -> enforcer
                                .getFilteredNamedPolicy("p", 0,
                                            policyModel.getSubject(), policyModel.getSource(),
                                            policyModel.getAction() == null || AclAction.ALL == policyModel.getAction() ? "" : policyModel.getAction().name(),
                                            policyModel.getAclType() == null || AclType.ALL == policyModel.getAclType() ? "" : policyModel.getAclType().getDesc())
                    )
                    .orElse(Collections.emptyList());
    }

}
