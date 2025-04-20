package com.dft.mom.domain.generator;

import com.dft.mom.web.exception.CommonException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

import static com.dft.mom.domain.util.CommonConstants.EPOCH;
import static com.dft.mom.web.exception.ExceptionType.TIME_INVALID;

public class IDGenerator implements IdentifierGenerator {

    private final long epoch = EPOCH;
    private final long serverId = 1L;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private final long serverIdBits = 5L;
    private final long sequenceBits = 12L;
    private final long maxSequence = ~(-1L << sequenceBits);

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
            sequence = 0;
        }

        lastTimestamp = timestamp;
        return ((timestamp - epoch) << (serverIdBits + sequenceBits)) | (serverId << sequenceBits) | sequence;
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return generateId();
    }
}