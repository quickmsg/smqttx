package io.github.quickmsg.common.acl;

/**
 * @author luxurong
 */
public enum AclPolicy {

    /**
     * no check
     */
    NONE,
    /**
     * disk file
     */
    File,

    /**
     * sql database
     */
    JDBC

}
