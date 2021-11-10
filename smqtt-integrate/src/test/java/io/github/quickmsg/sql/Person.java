package io.github.quickmsg.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.util.Map;

/**
 * @author luxurong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    @QuerySqlField
    private String username;

    @QuerySqlField(index = true)
    private int id;

    @QuerySqlField
    private int age;

    @QuerySqlField
    private String params;






}
