package io.github.quickmsg.common.interate1.job;

import java.lang.annotation.*;

/**
 * @author luxurong
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface JobFor {

    JobType jobType() default JobType.RUNNABLE_SINGLE;

    String name();

}
