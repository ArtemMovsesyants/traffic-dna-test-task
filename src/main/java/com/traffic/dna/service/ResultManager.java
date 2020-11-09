package com.traffic.dna.service;

import com.traffic.dna.dto.UserResult;

import java.util.Set;

public interface ResultManager {
    Set<UserResult> getUserInfo(String userId);

    Set<UserResult> getLevelInfo(int levelId);

    boolean setPerson(UserResult newUserResult);
}
