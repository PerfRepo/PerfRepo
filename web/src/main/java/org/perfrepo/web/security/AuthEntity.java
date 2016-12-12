package org.perfrepo.web.security;

import org.perfrepo.model.auth.AccessType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthEntity {

    AccessType accessType() default AccessType.WRITE;

    String messageKey() default "";

    String[] messageArgs() default {};

}
