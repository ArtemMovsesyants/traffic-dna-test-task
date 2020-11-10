package com.traffic.dna.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.log4j.Log4j2;

@Getter
@Log4j2
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserResult {
    @JsonProperty("user_id")
    String userId;

    @JsonProperty("level_id")
    int levelId;

    @JsonProperty("result")
    int result;

    @JsonIgnore
    public boolean isValid() {
        boolean isUserIdValid = userId != null && !userId.isEmpty();
        boolean isLevelIdValid = levelId > 0;
        boolean isResultValid = result >= 0;

        if (!isUserIdValid) {
            log.error("User Id '{}' is incorrect. User Id can't be null or empty", userId);
        }
        if (!isLevelIdValid) {
            log.error("Level Id '{}' is incorrect. Level Id should be bigger than 0", levelId);
        }
        if (!isResultValid) {
            log.error("Result '{}' is incorrect. Result Id can't be negative", result);
        }

        return isUserIdValid && isLevelIdValid && isResultValid;
    }


}
