package com.dft.mom.web.filter.security;

import com.dft.mom.domain.service.BlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import static com.dft.mom.domain.function.FunctionUtil.getClientIp;

@RequiredArgsConstructor
public class CustomHttpFirewall extends StrictHttpFirewall {

    private final BlacklistService blacklistService;

    @Override
    public FirewalledRequest getFirewalledRequest(HttpServletRequest request) throws RequestRejectedException {
        try {
            return super.getFirewalledRequest(request);
        } catch (RequestRejectedException ex) {
            String clientIp = getClientIp(request);
            blacklistService.addToBlacklist(clientIp);
            throw ex;
        }
    }
}