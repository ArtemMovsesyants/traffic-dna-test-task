package com.traffic.dna.service;

import com.traffic.dna.dto.UserResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import(SimpleResultManager.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SimpleResultManagerTest {

    @Autowired
    @Qualifier("simpleResultManager")
    private ResultManager resultManager;

    @Test
    void getUserInfo() {
        UserResult userResult1 = new UserResult("1", 1, 1);
        UserResult userResult2 = new UserResult("1", 2, 4);
        UserResult userResult3 = new UserResult("2", 4, 5);
        Set<UserResult> expectedResult = new TreeSet<>(Comparator.comparing(UserResult::getResult)
                .thenComparing(UserResult::getLevelId)
                .reversed());
        expectedResult.add(userResult1);
        expectedResult.add(userResult2);

        resultManager.setPerson(userResult1);
        resultManager.setPerson(userResult2);
        resultManager.setPerson(userResult3);
        Set<UserResult> actualResult = resultManager.getUserInfo("1");

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getLevelInfo() {
        Comparator.comparing(UserResult::getResult)
                .thenComparing(UserResult::getUserId)
                .reversed();

        UserResult userResult1 = new UserResult("1", 2, 1);
        UserResult userResult2 = new UserResult("1", 1, 4);
        UserResult userResult3 = new UserResult("2", 2, 5);
        Set<UserResult> expectedResult = new TreeSet<>(Comparator.comparing(UserResult::getResult)
                .thenComparing(UserResult::getUserId)
                .reversed());
        expectedResult.add(userResult1);
        expectedResult.add(userResult3);

        resultManager.setPerson(userResult1);
        resultManager.setPerson(userResult2);
        resultManager.setPerson(userResult3);
        Set<UserResult> actualResult = resultManager.getLevelInfo(2);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void setPerson() {
        UserResult userResult1 = new UserResult("1", 1, 1);
        UserResult userResult2 = new UserResult("1", 2, 2);
        boolean actualResult1 = resultManager.setPerson(userResult1);
        boolean actualResult2 = resultManager.setPerson(userResult2);
        assertThat(actualResult1 && actualResult2).isTrue();
    }

    @Test
    void setPerson2() {
        ReflectionTestUtils.setField(resultManager, "USER_RESULTS_LIMIT", 3);
        UserResult userResult1 = new UserResult("1", 1, 1);
        UserResult userResult3 = new UserResult("1", 2, 4);
        UserResult userResult5 = new UserResult("1", 2, 6);
        UserResult userResult2 = new UserResult("1", 2, 2);
        UserResult userResult4 = new UserResult("1", 2, 5);
        Set<UserResult> expectedResult = new TreeSet<>(Comparator.comparing(UserResult::getResult)
                .thenComparing(UserResult::getLevelId)
                .reversed());
        expectedResult.add(userResult3);
        expectedResult.add(userResult5);
        expectedResult.add(userResult4);

        resultManager.setPerson(userResult1);
        resultManager.setPerson(userResult2);
        resultManager.setPerson(userResult3);
        resultManager.setPerson(userResult4);
        resultManager.setPerson(userResult5);
        Set<UserResult> actualResult = resultManager.getUserInfo("1");

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void setPerson3() {
        ReflectionTestUtils.setField(resultManager, "USER_RESULTS_LIMIT", 3);
        UserResult userResult1 = new UserResult("1", 1, 10);
        UserResult userResult2 = new UserResult("1", 1, 15);
        UserResult userResult3 = new UserResult("1", 2, 20);
        UserResult userResult = new UserResult("1", 2, 5);

        resultManager.setPerson(userResult1);
        resultManager.setPerson(userResult2);
        resultManager.setPerson(userResult3);
        boolean actualResult = resultManager.setPerson(userResult);

        assertThat(actualResult).isFalse();
    }

}