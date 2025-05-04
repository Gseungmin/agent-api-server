package com.dft.mom.domain.util;

import java.util.List;
import java.util.Set;

public class PostConstants {

    // POST PRIORITY
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_MEDIUM = 1;
    public static final int PRIORITY_HIGH = 2;

    // POST IMPORTANCE
    public static final int DEFAULT_IMPORTANT = 0;
    public static final int HIGH_IMPORTANT = 1;

    // POST CAUTION
    public static final int DEFAULT_POST = 0;
    public static final int CAUTION = 1;

    // SUB_ITEM QUN
    public static final int NOT_QNA = 0;
    public static final int QNA = 1;

    // NUTRITION TAG
    public static final int NUTRIENT_POST_TAG = 0;  // 영양 성분 페이지 기본 태그

    public static final int BRAIN_DEVELOPMENT = 1000;  // 태아의 뇌와 신경 발달
    public static final int BLOOD = 1001;  // 혈액 및 빈혈 관리
    public static final int BONE_GROWTH = 1002;  // 뼈 건강 및 치아 관리
    public static final int FETAL_IMMUNITY = 1003;  // 아이 면역력

    // 영양 성분 태그 집합
    public static final Set<Integer> NUTRIENT_TAGS = Set.of(
            NUTRIENT_POST_TAG,
            BRAIN_DEVELOPMENT, BLOOD, BONE_GROWTH, FETAL_IMMUNITY
    );

    // BABY POST
    public static final int FETAL_GROWTH = 1000;  // 태아 성장
    public static final int MATERNAL_SYMPTOMS = 1001;  // 산모 증상
    public static final int PREGNANCY_RECOMMEND = 1002;  // 임신 추천사항
    public static final int PREGNANCY_PRECAUTIONS = 1003;  // 임신 주의사항
    public static final int BIRTH_PREPARATION = 1004;  // 출산 준비
    public static final int BIRTH_PREPARATION_ITEM = 1005;  // 출산 준비 아이템

    public static final int BABY_PARENTING = 1100;  // 육아
    public static final int BABY_ATTACHMENT = 1101;  // 애착 형성
    public static final int BABY_SLEEP = 1102;  // 아이 수면
    public static final int MOM_CARE = 1103;  // 산모관리
    public static final int PARENTING_PRECAUTIONS = 1104;  // 육아 주의사항
    public static final int PARENTING_PREPARATION_ITEM = 1105;  // 육아 준비 아이템

    public static final Set<Integer> FETAL_GUIDE_CATEGORIES = Set.of(
            FETAL_GROWTH, MATERNAL_SYMPTOMS, PREGNANCY_RECOMMEND,
            PREGNANCY_PRECAUTIONS, BIRTH_PREPARATION, BIRTH_PREPARATION_ITEM
    );

    public static final Set<Integer> BABY_GUIDE_CATEGORIES = Set.of(
            BABY_PARENTING, BABY_ATTACHMENT, BABY_SLEEP,
            MOM_CARE, PARENTING_PRECAUTIONS, PARENTING_PREPARATION_ITEM
    );

    // NUTRITION POST
    public static final int NUTRIENTS = 2000;  // 출산 전 영양 성분
    public static final int NUTRIENTS_CAUTION = 2002;  // 출산 전 영양 주의사항
    public static final int NUTRIENT_BEFORE_BIRTH = 2003;  // 출산 전 영양제

    public static final int BREAST_FEEDING = 2100;       // 모유 수유
    public static final int FORMULA_FEEDING = 2101;      // 분유 수유
    public static final int BABY_FOOD = 2102;            // 이유식
    public static final int NUTRIENT_AFTER_BIRTH = 2103; // 출산 후 영양지

    // 출산 전/후 카테고리 집합
    public static final Set<Integer> FETAL_NUTRITION_CATEGORIES = Set.of(
            NUTRIENTS, NUTRIENTS_CAUTION, NUTRIENT_BEFORE_BIRTH,
            BREAST_FEEDING, FORMULA_FEEDING, BABY_FOOD, NUTRIENT_AFTER_BIRTH
    );

    public static final Set<Integer> BABY_NUTRITION_CATEGORIES = Set.of(
            NUTRIENTS, NUTRIENTS_CAUTION, NUTRIENT_BEFORE_BIRTH,
            BREAST_FEEDING, FORMULA_FEEDING, BABY_FOOD, NUTRIENT_AFTER_BIRTH
    );

    // INSPECTION POST
    public static final int INSPECTION_AND_VACCINATIONS = 3000;  // 검사 및 접종
    public static final int FIND_NEARBY_HOSPITALS = 3002;  // 근처 병원 찾기

    public static final Set<Integer> INSPECTION_CATEGORIES = Set.of(
            INSPECTION_AND_VACCINATIONS, FIND_NEARBY_HOSPITALS
    );

    // PAGE STATUS
    public static final int TOTAL_PAGE_SIZE = 23;

    // PAGE TYPE
    public static final int TYPE_PREGNANCY_GUIDE = 0;
    public static final int TYPE_PREGNANCY_NUTRITION = 1;
    public static final int TYPE_INSPECTION = 2;
    public static final int TYPE_CHILDCARE_GUIDE = 3;
    public static final int TYPE_CHILDCARE_NUTRITION = 4;

    // PERIOD
    public static final int PERIOD_TOTAL = 0;

    // FETAL PERIOD
    public static final int FETAL_PERIOD_0_4 = 101;
    public static final int FETAL_PERIOD_5_8 = 102;
    public static final int FETAL_PERIOD_9_12 = 103;
    public static final int FETAL_PERIOD_13_16 = 104;
    public static final int FETAL_PERIOD_17_20 = 105;
    public static final int FETAL_PERIOD_21_24 = 106;
    public static final int FETAL_PERIOD_25_28 = 107;
    public static final int FETAL_PERIOD_29_32 = 108;
    public static final int FETAL_PERIOD_33_36 = 109;
    public static final int FETAL_PERIOD_37_40 = 110;

    // BABY PERIOD
    public static final int BABY_PERIOD_0_1 = 201;
    public static final int BABY_PERIOD_1_2 = 202;
    public static final int BABY_PERIOD_3_4 = 203;
    public static final int BABY_PERIOD_5_6 = 204;
    public static final int BABY_PERIOD_7_8 = 205;
    public static final int BABY_PERIOD_9_10 = 206;
    public static final int BABY_PERIOD_11_12 = 207;
    public static final int BABY_PERIOD_13_15 = 208;
    public static final int BABY_PERIOD_16_18 = 209;
    public static final int BABY_PERIOD_19_24 = 210;

    public static final List<Integer> FETAL_PERIOD_LIST = List.of(
            FETAL_PERIOD_0_4, FETAL_PERIOD_5_8, FETAL_PERIOD_9_12, FETAL_PERIOD_13_16,
            FETAL_PERIOD_17_20, FETAL_PERIOD_21_24, FETAL_PERIOD_25_28, FETAL_PERIOD_29_32, FETAL_PERIOD_33_36, FETAL_PERIOD_37_40
    );

    public static final List<Integer> BABY_PERIOD_LIST = List.of(
            BABY_PERIOD_0_1, BABY_PERIOD_1_2, BABY_PERIOD_3_4, BABY_PERIOD_5_6,
            BABY_PERIOD_7_8, BABY_PERIOD_9_10, BABY_PERIOD_11_12, BABY_PERIOD_13_15, BABY_PERIOD_16_18, BABY_PERIOD_19_24
    );
}