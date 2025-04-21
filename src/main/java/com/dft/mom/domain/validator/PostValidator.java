package com.dft.mom.domain.validator;

import com.dft.mom.domain.dto.baby.post.PostRowDto;
import com.dft.mom.web.exception.post.PageException;

import java.util.List;

import static com.dft.mom.domain.util.EntityConstants.*;
import static com.dft.mom.domain.util.PostConstants.*;
import static com.dft.mom.domain.validator.CommonValidator.validateId;
import static com.dft.mom.web.exception.ExceptionType.*;

public class PostValidator {

    public static void validateRows(List<PostRowDto> rows) {
        rows.forEach(PostValidator::validate);
    }

    public static void validate(PostRowDto dto) {
        Long id = dto.getItemId();
        validateId(id);
        validateTitle(dto.getTitle(), id);
        validateType(dto.getType(), id);
        validatePeriod(dto.getStartPeriod(), dto.getEndPeriod(), dto.getType(), id);

        if (dto.getCaution().equals(false)) {
            validateSummary(dto.getSummary(), id);
        }
    }

    public static void validateTitle(String title, Long id) {
        if (title == null || title.isEmpty() || title.length() > MAX_TITLE) {
            throw new PageException(
                    PAGE_TITLE_INVALID.getCode(),
                    "ID:" + id + " " + PAGE_TITLE_INVALID.getErrorMessage()
            );
        }
    }

    public static void validateSummary(String summary, Long id) {
        if (summary == null || summary.isEmpty() || summary.length() > MAX_SUMMARY) {
            throw new PageException(
                    PAGE_SUMMARY_INVALID.getCode(),
                    "ID:" + id + " " + PAGE_SUMMARY_INVALID.getErrorMessage()
            );
        }
    }

    public static void validateType(Integer type, Long id) {
        if (type == null || type < TYPE_PREGNANCY_GUIDE || type > TYPE_CHILDCARE_EXAM) {
            throw new PageException(
                    PAGE_TYPE_INVALID.getCode(),
                    "ID:" + id + " " + PAGE_TYPE_INVALID.getErrorMessage()
            );
        }
    }

    public static void validatePeriod(Integer start, Integer end, Integer type, Long id) {
        switch (type) {
            case TYPE_PREGNANCY_EXAM, TYPE_CHILDCARE_EXAM:
                if (start != PERIOD_TOTAL || end != PERIOD_TOTAL) {
                    validatePeriod(id);
                }
                break;
            case TYPE_PREGNANCY_GUIDE:
                if (start < FETAL_PERIOD_0_4 || start > FETAL_PERIOD_37_40 || end < FETAL_PERIOD_0_4 || end > FETAL_PERIOD_37_40) {
                    validatePeriod(id);
                }
                break;
            case TYPE_CHILDCARE_GUIDE:
                if (start < BABY_PERIOD_0_1 || start > BABY_PERIOD_19_24 || end < BABY_PERIOD_0_1 || end > BABY_PERIOD_19_24) {
                    validatePeriod(id);
                }
                break;
        }

        if (start > end) {
            validatePeriod(id);
        }
    }

    private static void validatePeriod(Long id) {
        throw new PageException(
                PAGE_PERIOD_INVALID.getCode(),
                "ID:" + id + " " + PAGE_PERIOD_INVALID.getErrorMessage()
        );
    }
}
