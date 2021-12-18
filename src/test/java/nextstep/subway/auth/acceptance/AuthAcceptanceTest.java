package nextstep.subway.auth.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.dto.TokenRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.member.MemberAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthAcceptanceTest extends AcceptanceTest {

    @DisplayName("Bearer Auth")
    @Test
    void myInfoWithBearerAuth() {

        //given
        ExtractableResponse<Response> createResponse = 회원_생성을_요청(EMAIL, PASSWORD, AGE);
        회원_생성됨(createResponse);
        TokenRequest tokenRequest = new TokenRequest(EMAIL, PASSWORD);

        //when
        ExtractableResponse<Response> 로그인 = 로그인_요청(tokenRequest);

        //then
        assertThat(로그인.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(로그인.body().jsonPath().getString("accessToken")).isNotEmpty();
    }

    @DisplayName("Bearer Auth 로그인 실패")
    @Test
    void myInfoWithBadBearerAuth() {

        //given
        ExtractableResponse<Response> createResponse = 회원_생성을_요청(EMAIL, PASSWORD, AGE);
        회원_생성됨(createResponse);
        final String NOT_PASSWORD = "notPassword";
        TokenRequest tokenRequest = new TokenRequest(EMAIL, NOT_PASSWORD);

        //when
        ExtractableResponse<Response> 로그인 = 로그인_요청(tokenRequest);

        //then
        assertThat(로그인.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("Bearer Auth 유효하지 않은 토큰")
    @Test
    void myInfoWithWrongBearerAuth() {

        //given
        final String INVALID_TOKEN = "awsda";
        ExtractableResponse<Response> createResponse = 회원_생성을_요청(EMAIL, PASSWORD, AGE);
        회원_생성됨(createResponse);

        String location = createResponse.header("Location");
        Long id = extractId(location);

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("email", EMAIL);
        params.put("age", AGE);

        //when
        ExtractableResponse<Response> response = 내_정보_조회_요청(createResponse, INVALID_TOKEN, EMAIL, AGE);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
    
    public static ExtractableResponse<Response> 로그인_요청(TokenRequest tokenRequest) {

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(tokenRequest)
                .post("/login/token")
                .then().log().all()
                .extract();

    }

}
