import ch.qos.logback.classic.Level;
import io.github.quickmsg.common.config.BootstrapConfig;
import io.github.quickmsg.core.Bootstrap;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.file_adapter.FileAdapter;

/**
 * @author luxurong
 * @date 2021/5/6 19:25
 * @description
 */
public class ClusterNode2 {

    @org.junit.Test
    public void startServer() throws InterruptedException {
        Model model = new Model();
        model.addDef("r", "r", "sub, obj, act");
        model.addDef("p", "p", " sub, obj, act, eft");
        model.addDef("g", "g", "_, _");
        model.addDef("e", "e", "some(where (p.eft == allow)) && !some(where (p.eft == deny))");
        model.addDef("m", "m", "r.act == p.act && keyMatch2(r.obj,p.obj)  && filter(r.sub, p.sub)");
        Enforcer enforcer =  new Enforcer(model, new FileAdapter("C:\\Users\\luxurong\\IdeaProjects\\smqttx\\config\\acl\\basic_policy.csv"));
//        enforcer.removeNamedPolicy( "p","client01", "topicA", "PUBLISH","allow");
        enforcer.addNamedPolicy( "p","client01", "topicA", "PUBLISH1","allow");

    }
}
