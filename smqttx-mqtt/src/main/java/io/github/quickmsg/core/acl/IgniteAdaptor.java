package io.github.quickmsg.core.acl;

import io.github.quickmsg.common.integrate.cache.IntegrateCache;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteSet;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.Adapter;
import org.casbin.jcasbin.persist.Helper;
import org.casbin.jcasbin.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
public class IgniteAdaptor implements Adapter {
    private IntegrateCache<String,Object> policyModels;

    private Object object = new Object();

    public IgniteAdaptor(IntegrateCache<String,Object> policyModels) {
        this.policyModels = policyModels;

    }

    @Override
    public void loadPolicy(Model model) {

        try {
            this.loadPolicyData(model, Helper::loadPolicyLine);
        } catch (Throwable var13) {
            throw var13;
        }


    }

    @Override
    public void savePolicy(Model model) {
        List<String> policy = new ArrayList();
        policy.addAll(this.getModelPolicy(model, "p"));
        policy.addAll(this.getModelPolicy(model, "g"));
        this.savePolicyFile(String.join("\n", policy));
    }

    private List<String> getModelPolicy(Model model, String ptype) {
        List<String> policy = new ArrayList();
        model.model.get(ptype).forEach((k, v) -> {
            List<String> p = v.policy.parallelStream().map(x -> k + ", " + Util.arrayToString(x)).collect(Collectors.toList());
            policy.addAll(p);
        });
        return policy;
    }

    private void loadPolicyData(Model model, Helper.loadPolicyLineHandler<String, Model> handler) {
        policyModels.forEach((x) -> {
            handler.accept(x.getKey(), model);
        });
    }

    private void savePolicyFile(String text) {
        policyModels.put(text,object);
    }

    @Override
    public void addPolicy(String sec, String ptype, List<String> rule) {
        String key = ptype+", "+Util.arrayToString(rule);
        policyModels.put(key,object);
    }

    @Override
    public void removePolicy(String sec, String ptype, List<String> rule) {
        String key = ptype+", "+Util.arrayToString(rule);
        policyModels.remove(key);
    }

    @Override
    public void removeFilteredPolicy(String sec, String ptype, int fieldIndex, String... fieldValues) {
        throw new UnsupportedOperationException("not implemented");
    }
}
