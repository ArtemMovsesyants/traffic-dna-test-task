package com.traffic.dna.service;

import com.traffic.dna.dto.UserResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Log4j2
@Service("simpleResultManager")
@PropertySource("classpath:constants.properties")
public class SimpleResultManager implements ResultManager {
    private static final Comparator<UserResult> USER_INFO_COMPARATOR;
    private static final Comparator<UserResult> LEVEL_INFO_COMPARATOR;
    private final Map<String, TreeSet<UserResult>> topResultsByUserId = new ConcurrentHashMap<>();
    private final Map<Integer, TreeSet<UserResult>> topResultsByLevelId = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    @Value("${limits.result.user}")
    private int USER_RESULTS_LIMIT;

    @Value("${limits.result.level}")
    private int LEVEL_RESULTS_LIMIT;

    static {
        USER_INFO_COMPARATOR = Comparator.comparing(UserResult::getResult)
                .thenComparing(UserResult::getLevelId)
                .reversed();
        LEVEL_INFO_COMPARATOR = Comparator.comparing(UserResult::getResult)
                .thenComparing(UserResult::getUserId)
                .reversed();
    }

    @Override
    public Set<UserResult> getUserInfo(String userId) {
        return topResultsByUserId.get(userId);
    }

    @Override
    public Set<UserResult> getLevelInfo(int levelId) {
        return topResultsByLevelId.get(levelId);
    }

    @Override
    public boolean setResult(UserResult newUserResult) {
        updateTopResultsByLevel(newUserResult);
        return updateTopResultsByUser(newUserResult);
    }

    private void updateTopResultsByLevel(UserResult newUserResult) {
        lock.lock();
        try {
            TreeSet<UserResult> topLevelResults = topResultsByLevelId.get(newUserResult.getLevelId());
            if (topLevelResults == null) {
                topLevelResults = new TreeSet<>(LEVEL_INFO_COMPARATOR);
                addNewResultForLevel(newUserResult, topLevelResults);
            } else if (topLevelResults.size() < LEVEL_RESULTS_LIMIT) {
                addNewResultForLevel(newUserResult, topLevelResults);
            } else if (topLevelResults.size() == LEVEL_RESULTS_LIMIT && doesNewResultHitTheTop(newUserResult, topLevelResults)) {
                topLevelResults.pollLast();
                addNewResultForLevel(newUserResult, topLevelResults);
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean updateTopResultsByUser(UserResult newUserResult) {
        lock.lock();
        try {
            TreeSet<UserResult> topUserResults = topResultsByUserId.get(newUserResult.getUserId());
            if (topUserResults == null) {
                topUserResults = new TreeSet<>(USER_INFO_COMPARATOR);
                return addNewResultForUser(newUserResult, topUserResults);
            } else if (topUserResults.size() < USER_RESULTS_LIMIT) {
                return addNewResultForUser(newUserResult, topUserResults);
            } else if (topUserResults.size() == USER_RESULTS_LIMIT && doesNewResultHitTheTop(newUserResult, topUserResults)) {
                topUserResults.pollLast();
                return addNewResultForUser(newUserResult, topUserResults);
            }

            log.warn(
                    "Top user results weren't updated, because result '{}' doesn't match the top {}",
                    newUserResult.getResult(),
                    USER_RESULTS_LIMIT);

            return false;
        } finally {
            lock.unlock();
        }
    }

    private void addNewResultForLevel(UserResult newUserResult, TreeSet<UserResult> userResults) {
        userResults.add(newUserResult);
        topResultsByLevelId.put(newUserResult.getLevelId(), userResults);
        log.info("New top level result was added");
    }

    private boolean addNewResultForUser(UserResult newUserResult, TreeSet<UserResult> userResults) {
        userResults.add(newUserResult);
        topResultsByUserId.put(newUserResult.getUserId(), userResults);
        log.info("New top user result was added");

        return true;
    }

    private boolean doesNewResultHitTheTop(UserResult newUserResult, TreeSet<UserResult> topResults) {
        int worstResult = topResults.last().getResult();
        return worstResult < newUserResult.getResult();
    }

}
