package io.github.quickmsg.core.http.actors.acl.model;

import io.github.quickmsg.common.acl.AclAction;
import lombok.Data;

/**
 * @author luxurong
 */
@Data
public class PolicyModel {

    private String subject;

    private String source;

    private AclAction action;

}