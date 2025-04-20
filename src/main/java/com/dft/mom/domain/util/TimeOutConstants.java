package com.dft.mom.domain.util;

public class TimeOutConstants {

    public static final long ONE_DAY = 24 * 60 * 60 * 1000;

    public static final long TIMEOUT_BLACKLIST = 10 * 60 * 1000L;
    public static final long ACCESS_TOKEN_EXPIRED = ONE_DAY * 30;
    public static final long REFRESH_TOKEN_EXPIRED = ONE_DAY * 60;
}