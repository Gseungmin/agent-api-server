package com.dft.mom.domain.service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.dft.mom.domain.util.TimeOutConstants.TIMEOUT_BLACKLIST;

@Service
public class BlacklistService {
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void addToBlacklist(String ip) {
        blacklist.put(ip, System.currentTimeMillis() + TIMEOUT_BLACKLIST);
    }

    public boolean isBlacklisted(String ip) {
        Long expiryTime = blacklist.get(ip);

        if (expiryTime == null) {
            return false;
        }

        if (System.currentTimeMillis() > expiryTime) {
            blacklist.remove(ip);
            return false;
        }
        return true;
    }

    @Scheduled(fixedRate = TIMEOUT_BLACKLIST)
    private void cleanupBlacklist() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> entry.getValue() < now);
    }
}

