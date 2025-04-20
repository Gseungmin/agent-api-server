package com.dft.mom.domain.generator;

import com.dft.mom.web.exception.CommonException;
import org.springframework.stereotype.Component;

import static com.dft.mom.domain.util.CommonConstants.EPOCH;
import static com.dft.mom.web.exception.ExceptionType.TIME_INVALID;

@Component
public class CodeGenerator  {

    private final long epoch = EPOCH;
    private final long serverId = 1L;
    private final long serverIdBits = 5L;
    private final long sequenceBits = 12L;
    private final long maxSequence = ~(-1L << sequenceBits);
    private final long serverIdShift = sequenceBits;
    private final long timestampLeftShift = sequenceBits + serverIdBits;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private final Object lock = new Object();

    private long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    private long waitForNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimeMillis();
        }
        return timestamp;
    }

    public synchronized long generateId() {
        long timestamp = currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new CommonException(TIME_INVALID.getCode(), TIME_INVALID.getErrorMessage());
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                timestamp = waitForNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << timestampLeftShift) | (serverId << serverIdShift) | sequence;
    }

    public String generateBase32Id() {
        long id = generateId();
        return encodeBase36(id);
    }

    public String encodeBase36(long id) {
        final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            int index = (int)(id % 36);
            sb.append(characters.charAt(index));
            id /= 36;
        }
        return sb.reverse().toString();
    }
}