package com.dft.mom.domain.validator;

import com.dft.mom.domain.dto.post.InspectionRowDto;
import com.dft.mom.domain.dto.post.NutritionRowDto;
import com.dft.mom.domain.dto.post.PostRowDto;
import com.dft.mom.web.exception.post.PageException;

import java.util.List;
import java.util.Map;

import static com.dft.mom.domain.util.EntityConstants.*;
import static com.dft.mom.domain.util.PostConstants.*;
import static com.dft.mom.domain.validator.CommonValidator.validateId;
import static com.dft.mom.web.exception.ExceptionType.*;

public class PostValidator {

    public static void validateRows(List<PostRowDto> rows) {
        rows.forEach(PostValidator::validate);
    }

    public static void validateNutritionRows(List<NutritionRowDto> rows) {
        rows.forEach(PostValidator::validateNutrition);
    }

    public static void validateInspectionRows(List<InspectionRowDto> rows) {
        rows.forEach(PostValidator::validateInspection);
    }

    private static void validate(PostRowDto dto) {
        Long id = dto.getItemId();
        validateId(id);
        validateTitle(dto.getTitle(), id);
        validateType(dto.getType(), id);
        validatePeriod(dto.getStartPeriod(), dto.getEndPeriod(), dto.getType(), id);

        if (dto.getCaution().equals(false)) {
            validateSummary(dto.getSummary(), id);
        }
    }

    private static void validateNutrition(NutritionRowDto dto) {
        Long id = dto.getItemId();
        validateId(id);
        validateTitle(dto.getTitle(), id);
        validateTag(dto.getTag(), id);
        validateNutritionCategory(dto.getCategory(), id);

        if (!dto.getCategory().equals(B_B_CAUTION)) {
            validateSummary(dto.getSummary(), id);
        }
    }

    private static void validateInspection(InspectionRowDto dto) {
        Long id = dto.getItemId();
        validateId(id);
        validateTitle(dto.getTitle(), id);
        validateInspectionStartEnd(dto.getStart(), dto.getEnd(), id);
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
        if (type == null || type < TYPE_PREGNANCY_GUIDE || type > TYPE_CHILDCARE_NUTRITION) {
            throw new PageException(
                    PAGE_TYPE_INVALID.getCode(),
                    "ID:" + id + " " + PAGE_TYPE_INVALID.getErrorMessage()
            );
        }
    }

    public static void validatePeriod(Integer start, Integer end, Integer type, Long id) {
        switch (type) {
            case TYPE_INSPECTION:
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

    private static void validateTag(Integer tag, Long id) {
        if (tag == null || !NUTRIENT_TAGS.contains(tag)) {
            throw new PageException(
                    PAGE_TAG_INVALID.getCode(),
                    "ID:" + id + " " + PAGE_TAG_INVALID.getErrorMessage()
            );
        }
    }

    private static void validateNutritionCategory(Integer category, Long id) {
        if (category == null || !BIRTH_CATEGORIES.contains(category)) {
            throw new PageException(
                    PAGE_CATEGORY_INVALID.getCode(),
                    "ID:" + id + " " + PAGE_CATEGORY_INVALID.getErrorMessage()
            );
        }
    }

    private static void validateInspectionStartEnd(Integer start, Integer end, Long id) {
        if (start == null || end == null || start > end || start < 10000 || end > 20024) {
            throw new PageException(
                    PAGE_TIME_INVALID.getCode(),
                    "ID:" + id + " " + PAGE_TIME_INVALID.getErrorMessage()
            );
        }
    }

    private static final Map<Integer, List<Integer>> VALID_PERIODS = Map.of(
            TYPE_INSPECTION,         List.of(PERIOD_TOTAL),
            TYPE_PREGNANCY_GUIDE,    FETAL_PERIOD_LIST,
            TYPE_CHILDCARE_GUIDE,    BABY_PERIOD_LIST,
            TYPE_PREGNANCY_NUTRITION, List.of(PERIOD_TOTAL),
            TYPE_CHILDCARE_NUTRITION, List.of(PERIOD_TOTAL)
    );

    public static void validateTypeAndPeriod(Integer type, Integer period) {
        if (!VALID_PERIODS.containsKey(type)) {
            throw new PageException(PAGE_NOT_EXIST.getCode(), PAGE_NOT_EXIST.getErrorMessage());
        }

        if (!VALID_PERIODS.get(type).contains(period)) {
            throw new PageException(PAGE_NOT_EXIST.getCode(), PAGE_NOT_EXIST.getErrorMessage());
        }
    }
}
