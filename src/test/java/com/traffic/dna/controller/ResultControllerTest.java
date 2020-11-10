package com.traffic.dna.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic.dna.dto.UserResult;
import com.traffic.dna.service.ResultManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ResultController.class)
class ResultControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    @Qualifier("simpleResultManager")
    private ResultManager resultManager;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void setPerson_200_ok() throws Exception {
        UserResult userResult = new UserResult("5", 5, 5);

        Mockito.when(resultManager.setResult(userResult)).thenReturn(true);
        MockHttpServletResponse response = mockMvc.perform(post("/setInfo")
                .contentType("application/json")
                .content(mapper.writeValueAsBytes(userResult)))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        boolean actualResult = mapper.readValue(response.getContentAsByteArray(), Boolean.class);

        Mockito.verify(resultManager, times(1)).setResult(userResult);
        assertThat(actualResult).isTrue();
    }

    @Test
    void setPerson_400_ok() throws Exception {
        UserResult userResult = new UserResult("5", 5, -5);

        Mockito.when(resultManager.setResult(userResult)).thenReturn(false);
        MockHttpServletResponse response = mockMvc.perform(post("/setInfo")
                .contentType("application/json")
                .content(mapper.writeValueAsBytes(userResult)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();
        boolean actualResult = mapper.readValue(response.getContentAsByteArray(), Boolean.class);

        Mockito.verify(resultManager, times(0)).setResult(userResult);
        assertThat(actualResult).isFalse();
    }

    @Test
    void getUserInfo_200_ok() throws Exception {
        Set<UserResult> expectedResult = new HashSet<>();
        expectedResult.add(new UserResult("5", 5, 5));

        Mockito.when(resultManager.getUserInfo("1")).thenReturn(expectedResult);
        MockHttpServletResponse response = mockMvc.perform(get("/userInfo/1")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        Set<UserResult> actualResult = mapper.readValue(response.getContentAsByteArray(), new TypeReference<Set<UserResult>>() {
        });

        Mockito.verify(resultManager, times(1)).getUserInfo("1");
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void levelInfo_200_ok() throws Exception {
        Set<UserResult> expectedResult = new HashSet<>();
        expectedResult.add(new UserResult("5", 5, 5));

        Mockito.when(resultManager.getLevelInfo(1)).thenReturn(expectedResult);
        MockHttpServletResponse response = mockMvc.perform(get("/levelInfo/1")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        Set<UserResult> actualResult = mapper.readValue(response.getContentAsByteArray(), new TypeReference<Set<UserResult>>() {
        });

        Mockito.verify(resultManager, times(1)).getLevelInfo(1);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void levelInfo_400_bad() throws Exception {
        String expectedResult = "LevelId Id is incorrect";

        MockHttpServletResponse response = mockMvc.perform(get("/levelInfo/-1")
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();
        String actualResult = response.getContentAsString();

        Mockito.verify(resultManager, times(0)).getLevelInfo(-1);
        assertThat(actualResult).isEqualTo(expectedResult);
    }
}