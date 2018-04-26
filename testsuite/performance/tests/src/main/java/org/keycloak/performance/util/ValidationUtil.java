package org.keycloak.performance.util;

import org.apache.commons.validator.routines.IntegerValidator;
import org.apache.commons.validator.routines.LongValidator;

/**
 *
 * @author tkyjovsk
 */
public class ValidationUtil {

    public static final IntegerValidator VALIDATE_INT = IntegerValidator.getInstance();
    public static final LongValidator VALIDATE_LONG = LongValidator.getInstance();

}
