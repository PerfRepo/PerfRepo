package org.perfrepo.web.rest.validation;


import org.perfrepo.dto.test.TestDto;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.util.MessageUtils;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class UidUniqueValidator implements ConstraintValidator<TestUidUnique, TestDto> {

    private String message;
    @Inject
    private TestService testService;

    @Override
    public void initialize(TestUidUnique constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(TestDto testDto, ConstraintValidatorContext context) {
        // only duplicate value is invalid
        if (testDto == null || testDto.getUid() == null) {
            return true;
        }

        if (testService.getTestByUID(testDto.getUid()) != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MessageUtils.getMessage(message, testDto.getUid()))
                    .addPropertyNode("uid").addConstraintViolation();
            return false;
        }

        return true;
    }
}