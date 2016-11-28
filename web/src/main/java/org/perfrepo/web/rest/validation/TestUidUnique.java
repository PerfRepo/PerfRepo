package org.perfrepo.web.rest.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Constraint(validatedBy = {UidUniqueValidator.class})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface TestUidUnique {
    String message() default "serviceException.testUidExists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}