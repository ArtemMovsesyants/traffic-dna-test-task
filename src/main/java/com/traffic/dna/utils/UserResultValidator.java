package com.traffic.dna.utils;

import com.traffic.dna.dto.UserResult;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class UserResultValidator {

    public static boolean isUserResultValid(UserResult newUserResult) {
        String userId = newUserResult.getUserId();
        int levelId = newUserResult.getLevelId();
        int result = newUserResult.getResult();

        if (userId == null || userId.isEmpty()) {
            log.error("User Id '{}' is incorrect. User Id can't be null or empty", userId);
            return false;
        }
        if (levelId < 0) {
            log.error("Level Id '{}' is incorrect. Level Id should be bigger than 0", levelId);
            return false;
        }
        if (result < 0) {
            log.error("Result '{}' is incorrect. Result Id can't be negative", result);
            return false;
        }

        return true;
    }

}
