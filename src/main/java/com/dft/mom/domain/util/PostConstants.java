package com.dft.mom.domain.util;

import java.util.List;

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
    public static final int NUTRIENT_POST_TAG = 100;  // 영양 성분 페이지 기본 태그

    public static final int BONE_GROWTH = 1000;  // 골격 뼈 성장
    public static final int BRAIN_DEVELOPMENT = 1001;  // 뇌 발당
    public static final int FETAL_IMMUNITY = 1002;  // 아이 면역력
    public static final int SKIN = 1003;  // 피부 조직

    public static final int CARBOHYDRATES = 1100;  // 탄수화물
    public static final int PROTEIN = 1101;  // 단백질
    public static final int FAT = 1102;  // 지방
    public static final int VITAMINS = 1103;  // 비타민
    public static final int FIBER = 1104;  // 섬유질

    public static final int MOTHER_RECOVERY = 1200;  // 산모 회복
    public static final int MILK_QUALITY = 1201;  // 모유 품질
    public static final int MOTHER_IMMUNITY = 1202;  // 면역력
    public static final int MENTAL_HEALTH = 1203;  // 정신 건강

    // BABY POST
    public static final int FETAL_GROWTH = 1000;  // 태아 성장
    public static final int MATERNAL_SYMPTOMS = 1001;  // 산모 증상
    public static final int PREGNANCY_RECOMMEND = 1002;  // 임신 추천사항
    public static final int PREGNANCY_PRECAUTIONS = 1003;  // 임신 주의사항
    public static final int BIRTH_PREPARATION = 1004;  // 출산 준비
    public static final int BIRTH_PREPARATION_ITEM = 1005;  // 출산 준비 아이템

    public static final int BABY_PARENTING = 1100;  // 육아
    public static final int BABY_SLEEP = 1101;  // 아이 수면
    public static final int POSTPARTUM_CARE = 1102;  // 산후관리
    public static final int PARENTING_PRECAUTIONS = 1103;  // 육아 주의사항
    public static final int PARENTING_PREPARATION_ITEM = 1104;  // 육아 준비 아이템

    // NUTRITION POST (B_B : Before Birth, A_B : After Birth)
    public static final int B_B_NUTRIENTS = 2000;  // 출산 전 영양 성분
    public static final int B_B_FOOD = 2001;  // 출산 전 음식
    public static final int B_B_CAUTION = 2002;  // 출산 전 영양 주의사항
    public static final int B_B_NUTRIENT_ITEM = 2003;  // 출산 전 영양제

    public static final int A_B_FOOD = 2100;  // 출산 후 아이 식사
    public static final int A_B_NUTRIENT = 2101;  // 출산 후 산모 영양 성분
    public static final int A_B_CAUTION = 2102;  // 출산 후 산모 및 태아 영양 주의사항
    public static final int A_B_NUTRIENT_ITEM = 2103;  // 출산 후 영양지

    // INSPECTION POST
    public static final int INSPECTION_AND_VACCINATIONS = 3000;  // 검사 및 접종
    public static final int INSPECTION_TABLE = 3001;  // 시기별 검사표
    public static final int FIND_NEARBY_HOSPITALS = 3002;  // 근처 병원 찾기

    // PAGE STATUS
    public static final int TOTAL_PAGE_SIZE = 30;

    // PAGE TYPE
    public static final int TYPE_PREGNANCY_GUIDE = 0;
    public static final int TYPE_PREGNANCY_NUTRITION = 1;
    public static final int TYPE_PREGNANCY_EXAM = 2;
    public static final int TYPE_CHILDCARE_GUIDE = 3;
    public static final int TYPE_CHILDCARE_NUTRITION = 4;
    public static final int TYPE_CHILDCARE_EXAM = 5;

    // FETAL PERIOD
    public static final int FETAL_PERIOD_TOTAL = 100;
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
    public static final int BABY_PERIOD_TOTAL = 200;
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
            FETAL_PERIOD_TOTAL, FETAL_PERIOD_0_4, FETAL_PERIOD_5_8, FETAL_PERIOD_9_12, FETAL_PERIOD_13_16,
            FETAL_PERIOD_17_20, FETAL_PERIOD_21_24, FETAL_PERIOD_25_28, FETAL_PERIOD_29_32, FETAL_PERIOD_33_36, FETAL_PERIOD_37_40
    );

    public static final List<Integer> BABY_PERIOD_LIST = List.of(
            BABY_PERIOD_TOTAL, BABY_PERIOD_0_1, BABY_PERIOD_1_2, BABY_PERIOD_3_4, BABY_PERIOD_5_6,
            BABY_PERIOD_7_8, BABY_PERIOD_9_10, BABY_PERIOD_11_12, BABY_PERIOD_13_15, BABY_PERIOD_16_18, BABY_PERIOD_19_24
    );
}