package ru.company.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class Synchronizer {
    private static final Map<Long, ReentrantLock> LOCKED_ACCOUNTS = new ConcurrentHashMap<>();

    public void synchronize(Long account) {
        ReentrantLock lock = LOCKED_ACCOUNTS.get(account);
        if (lock == null) {
            lock = new ReentrantLock();
            LOCKED_ACCOUNTS.put(account, lock);
        }
        lock.lock();
    }

    public void synchronize(Long accountFrom, Long accountTo) {
        // Specific lock order to avoid thread lock
        Long minAccountId = Math.min(accountFrom, accountTo);
        Long maxAccountId = Math.max(accountFrom, accountTo);
        synchronize(minAccountId);
        synchronize(maxAccountId);
    }

    public void unlock(Long account) {
        ReentrantLock lock = null;
        try {
            lock = Optional.ofNullable(LOCKED_ACCOUNTS.remove(account))
                    .orElseThrow();
        } catch (Exception e) {
            log.error("Cannot release lock for key={}", account);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    public void unlock(Long accountFrom, Long accountTo) {
        unlock(accountFrom);
        unlock(accountTo);
    }
}
