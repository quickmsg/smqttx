package io.github.quickmsg.common.sql;

import lombok.Data;

/**
 * @author luxurong
 */
@Data
public class ConnectionQueryModel extends PageRequest {


    private String clientId;

    private String nodeIp;

    private String clientIp;


}
