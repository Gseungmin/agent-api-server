package com.dft.mom.web.filter.apple;

import com.dft.mom.web.exception.member.LoginException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.dft.mom.web.exception.ExceptionType.LOGIN_FAILED;

@Getter @Setter @AllArgsConstructor
public class ApplePublicKeyResponse {
    private List<ApplePublicKey> keys;

    public ApplePublicKeyResponse() {
    }

    public ApplePublicKey getMatchedKey(String kid, String alg) {
        return keys.stream()
                .filter(key -> key.getKid().equals(kid) && key.getAlg().equals(alg))
                .findFirst()
                .orElseThrow(() ->
                        new LoginException(LOGIN_FAILED.getCode(), LOGIN_FAILED.getErrorMessage()));
    }
}