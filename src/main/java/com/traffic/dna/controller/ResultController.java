package com.traffic.dna.controller;

import com.traffic.dna.dto.UserResult;
import com.traffic.dna.service.ResultManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Log4j2
@RestController
public class ResultController {
    private final ResultManager resultManager;

    public ResultController(@Qualifier("simpleResultManager") ResultManager resultManager) {
        this.resultManager = resultManager;
    }

    @PostMapping("/setInfo")
    public ResponseEntity<Boolean> setPerson(@RequestBody UserResult newUserResult) {
        log.info("'Set info' request was received");
        if (!newUserResult.isValid()) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(resultManager.setPerson(newUserResult), HttpStatus.OK);
    }

    @GetMapping("/userInfo/{userId}")
    public ResponseEntity<Object> getUserInfo(@PathVariable String userId) {
        log.info("'Get user info' request was received");
        Set<UserResult> userInfo = resultManager.getUserInfo(userId);

        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    @GetMapping("/levelInfo/{levelId}")
    public ResponseEntity<Object> levelInfo(@PathVariable int levelId) {
        log.info("'Get level info' request was received");

        if (levelId < 1) {
            log.error("LevelId Id '{}' is incorrect. Level Id should be bigger than 0", levelId);
            return new ResponseEntity<>("LevelId Id is incorrect", HttpStatus.BAD_REQUEST);
        }

        Set<UserResult> levelInfo = resultManager.getLevelInfo(levelId);
        return new ResponseEntity<>(levelInfo, HttpStatus.OK);
    }

}
