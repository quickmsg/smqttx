package io.github.quickmsg.common.sql;

import lombok.Data;

/**
 * @author luxurong
 */
@Data
public class PageRequest{

    private int pageNumber = 0;

    private int pageSize = 20;

}
