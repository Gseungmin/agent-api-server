package com.dft.mom.domain.validator;

import com.dft.mom.web.exception.CommonException;
import static com.dft.mom.web.exception.ExceptionType.*;

public class CommonValidator {
    public static void validateId(Long id) {
        if (id == null) {
            throw new CommonException(ID_INVALID.getCode(), ID_INVALID.getErrorMessage());
        }
    }
}
