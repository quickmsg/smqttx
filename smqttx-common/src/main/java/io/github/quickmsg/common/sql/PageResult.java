package io.github.quickmsg.common.sql;

import lombok.Data;

import java.util.List;

/**
 * @author luxurong
 */
@Data
public class PageResult<T> {

    private int pageNumber = 0;

    private int pageSize = 20;

    private int totalSize = 0;

    private int totalPages = 0;

    private List<T> content;


}
