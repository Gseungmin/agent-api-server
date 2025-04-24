package com.dft.mom.domain.function;

import java.time.LocalDate;

public class EntityFunctionUtil {
    public static LocalDate getLastMenstrual(LocalDate expectedBirth, LocalDate providedLastMenstrual) {
        if (expectedBirth != null) {
            return expectedBirth.minusWeeks(40);
        }
        return providedLastMenstrual;
    }
}