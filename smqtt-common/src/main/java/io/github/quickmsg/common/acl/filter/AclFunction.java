package io.github.quickmsg.common.acl.filter;

import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;
import org.casbin.jcasbin.util.function.CustomFunction;

import java.util.Map;

/**
 * @author luxurong
 */
public class AclFunction extends CustomFunction {

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        String requestSubject = FunctionUtils.getStringValue(arg1, env);
        String subject = FunctionUtils.getStringValue(arg2, env);
        return super.call(env, arg1, arg2);
    }

    @Override
    public String getName() {
        return "filter";
    }
}
