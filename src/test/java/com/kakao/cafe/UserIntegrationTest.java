package com.kakao.cafe;

import com.kakao.cafe.dto.user.UserCreationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("중복없는 입력값으로 회원가입 성공")
    void 회원가입_성공() throws Exception {
        //given
        String email = "testEmail@test.com";
        String nickname = "testUser";
        String password = "testPassword";
        UserCreationDto createRequest = UserCreationDto.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .build();
        System.out.println(createRequest.toString());

        //when
        ResultActions resultActions = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print());
        //then
        resultActions
                .andExpect(status().is3xxRedirection());
    }


}
