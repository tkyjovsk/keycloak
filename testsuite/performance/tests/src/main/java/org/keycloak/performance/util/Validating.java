package org.keycloak.performance.util;

import org.apache.commons.validator.routines.IntegerValidator;
import org.apache.commons.validator.routines.LongValidator;

/**
 *
 * @author tkyjovsk
 */
public interface Validating {

    default public IntegerValidator validateInt() {
        return IntegerValidator.getInstance();
    }

    default public LongValidator validateLong() {
        return LongValidator.getInstance();
    }

}
