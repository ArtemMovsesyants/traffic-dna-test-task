package com.traffic.dna.dto;

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

}
