package com.dft.mom.domain.util;

import java.util.Set;

public class CommonConstants {

    //SOCIAL LOGIN URL
    public static final String KAKAO_API = "https://kapi.kakao.com/v2/user/me";
    public static final String APPLE_API = "https://appleid.apple.com/auth/keys";

    //TOKEN UTIL
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";

    //ROUTE FOR NO AUTH
    public static final Set<String> POSSIBLE_GET_ROUTE = Set.of();

    //EPOCH
    public static final long EPOCH = 1740787200000L;
}