package io.github.quickmsg.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.util.List;

/**
 * @author luxurong
 */
@Data
public class Message {


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Person{

        @QuerySqlField(index = true)
        protected String username;

        @QuerySqlField(index = true)
        protected int id;

        @QuerySqlField(index = true)
        protected int age;

        @QuerySqlField
        private List<String> params;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PublishPerson {

        @QuerySqlField(index = true)
        private String username;

        @QuerySqlField(index = true)
        private int id;

        @QuerySqlField(index = true)
        private int age;

        @QuerySqlField
        private List<String> params;
    }







}
