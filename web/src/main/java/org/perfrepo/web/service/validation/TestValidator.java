package org.perfrepo.web.service.validation;

import org.perfrepo.model.Test;
import org.perfrepo.web.dao.TestDAO;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestValidator implements ConstraintValidator<ValidTest, Test> {

    private Set<ValidationType> type;

    @Inject
    private TestDAO testDAO;

    @Override
    public void initialize(ValidTest constraintAnnotation) {
        type = new HashSet<>(Arrays.asList(constraintAnnotation.type()));
    }

    @Override
    public boolean isValid(Test value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        if (type.contains(ValidationType.ID_NULL) && value.getId() != null) {
            return false;
        }

        if (type.contains(ValidationType.EXISTS) && testDAO.get(value.getId()) == null) {
            return false;
        }

        if (type.contains(ValidationType.FULL_CHECK)) {
            //TODO: implement full check
            System.out.println("Not implemented yet, just a placeholder because of checkstyle");
        }

        return true;
    }
}
