package com.traffic.dna.service;

import com.traffic.dna.dto.UserResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service("simpleResultManager")
@PropertySource("classpath:constants.properties")
public class SimpleResultManager implements ResultManager {
    private static final Comparator<UserResult> USER_INFO_COMPARATOR;
    private static final Comparator<UserResult> LEVEL_INFO_COMPARATOR;
    private final Map<String, TreeSet<UserResult>> topResultsByUserId = new HashMap<>();

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
        return topResultsByUserId.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(userResult -> userResult.getLevelId() == levelId)
                .limit(LEVEL_RESULTS_LIMIT)
                .collect(Collectors.toCollection(() -> new TreeSet<>(LEVEL_INFO_COMPARATOR)));
    }

    @Override
    public boolean setPerson(UserResult newUserResult) {
        TreeSet<UserResult> topUserResults = topResultsByUserId.get(newUserResult.getUserId());
        if (topUserResults == null) {
            topUserResults = new TreeSet<>(USER_INFO_COMPARATOR);
            return addNewResultForUser(newUserResult, topUserResults);
        } else if (topUserResults.size() < USER_RESULTS_LIMIT) {
            return addNewResultForUser(newUserResult, topUserResults);
        } else if (topUserResults.size() == USER_RESULTS_LIMIT && doesNewResultHitTheTop(newUserResult, topUserResults)) {
            return updateTopResults(newUserResult, topUserResults);
        }
        log.warn(
                "Top user results weren't updated, because result '{}' doesn't match the top {}",
                newUserResult.getResult(),
                USER_RESULTS_LIMIT);

        return false;
    }

    private boolean updateTopResults(UserResult newUserResult, TreeSet<UserResult> userResults) {
        userResults.pollLast();
        return addNewResultForUser(newUserResult, userResults);
    }

    private boolean addNewResultForUser(UserResult newUserResult, TreeSet<UserResult> userResults) {
        userResults.add(newUserResult);
        topResultsByUserId.put(newUserResult.getUserId(), userResults);
        log.info("New result was added");

        return true;
    }

    private boolean doesNewResultHitTheTop(UserResult newUserResult, TreeSet<UserResult> topResults) {
        int worstResult = topResults.last().getResult();
        return worstResult < newUserResult.getResult();
    }

}
