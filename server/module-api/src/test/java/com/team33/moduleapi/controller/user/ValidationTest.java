// package com.team33.moduleapi.controller.user;
//
// import static io.restassured.RestAssured.given;
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.assertj.core.api.Assertions.tuple;
//
// import com.team33.moduleapi.controller.ApiTest;
// import com.team33.modulecore.user.dto.UserPostDto;
// import io.restassured.response.ExtractableResponse;
// import io.restassured.response.Response;
// import java.util.List;
// import java.util.stream.Stream;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.Arguments;
// import org.junit.jupiter.params.provider.MethodSource;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
//
// class ValidationTest extends ApiTest {
//
//     private static Stream<Arguments> provideUserPostDtoWrongEmail() {
//         UserPostDto joinDto1 = join("testgmail.com", "sdf", "010-1111-1111");
//         UserPostDto joinDto2 = join("", "sdf", "010-0000-1111");
//
//         return Stream.of(
//             Arguments.of(joinDto1),
//             Arguments.of(joinDto2)
//         );
//     }
//
//     @DisplayName("이메일 형식(또는 공백)을 지키지 않으면 예외를 던진다.")
//     @ParameterizedTest
//     @MethodSource("provideUserPostDtoWrongEmail")
//     void test1(UserPostDto joinDto) throws Exception {
//
//         //@formatter:off
//             ExtractableResponse<Response> response =
//             given()
//                 .log().all()
//                 .contentType(MediaType.APPLICATION_JSON_VALUE)
//                 .body(joinDto)
//             .when()
//                 .post("/users")
//             .then()
//                 .log().all()
//                 .statusCode(HttpStatus.BAD_REQUEST.value())
//                 .extract();
//             //@formatter:on
//
//         //then
//         int status = response.jsonPath().get("status");
//         List<String> list = response.jsonPath().getList("customFieldErrors");
//
//         assertThat(status).isEqualTo(400);
//         assertThat(list)
//             .extracting("field", "rejectedValue", "reason")
//             .containsAnyOf(
//                 tuple("email", "testgmail.com", "올바른 이메일 형식이 아닙니다."),
//                 tuple("email", "", "공백일 수 없습니다")
//             );
//     }
//
//     @DisplayName("이름은 공백을 갖는다면 예외를 던진다.")
//     @Test
//     void test8() throws Exception {
//
//         UserPostDto joinDto = join("test@gmail.com", "sdf", "010-1111-1111");
//         joinDto.setRealName("홍 길동");
//
//         //@formatter:off
//             ExtractableResponse<Response> response =
//                 given()
//                     .log().all()
//                     .contentType(MediaType.APPLICATION_JSON_VALUE)
//                     .body(joinDto)
//                 .when()
//                     .post("/users")
//                 .then()
//                     .log().all()
//                     .statusCode(HttpStatus.BAD_REQUEST.value())
//                     .extract();
//             //@formatter:on
//
//         //then
//         int status = response.jsonPath().get("status");
//         List<String> list = response.jsonPath().getList("customFieldErrors");
//
//         assertThat(status).isEqualTo(400);
//         assertThat(list)
//             .extracting("field", "rejectedValue", "reason")
//             .containsExactlyInAnyOrder(
//                 tuple("realName", "홍 길동", "공백이 있어서는 안됩니다.")
//             );
//     }
//
//     @DisplayName("닉네임이 공백이면 예외를 던진다.")
//     @Test
//     void test2() throws Exception {
//
//         UserPostDto joinDto = join("test@gmail.com", "", "010-1111-1111");
//
//         //@formatter:off
//             ExtractableResponse<Response> response =
//             given()
//                     .log().all()
//                     .contentType(MediaType.APPLICATION_JSON_VALUE)
//                     .body(joinDto)
//             .when()
//                      .post("/users")
//             .then()
//                     .log().all()
//                     .statusCode(HttpStatus.BAD_REQUEST.value())
//                     .extract();
//             //@formatter:on
//
//         //then
//         int status = response.jsonPath().get("status");
//         List<String> list = response.jsonPath().getList("customFieldErrors");
//
//         assertThat(status).isEqualTo(400);
//         assertThat(list)
//             .extracting("field", "rejectedValue", "reason")
//             .containsExactlyInAnyOrder(
//                 tuple("displayName", "", "닉네임은 공백일 수 없습니다.")
//             );
//     }
//
//
//     private static Stream<Arguments> provideUserPostDtoWrongPhone() {
//         UserPostDto joinDto1 = join("test@gmail.com", "sdf", "010-11111-1111");
//         UserPostDto joinDto2 = join("test@gmail.com", "sdf", "");
//
//         return Stream.of(
//             Arguments.of(joinDto1),
//             Arguments.of(joinDto2)
//         );
//     }
//
//     @DisplayName("지정한 연락처 형식이 아닐 경우 예외를 던진다.")
//     @ParameterizedTest
//     @MethodSource("provideUserPostDtoWrongPhone")
//     void test3(UserPostDto postDto) throws Exception {
//
//         //@formatter:off
//         ExtractableResponse<Response> response =
//             given()
//                 .log().all()
//                 .contentType(MediaType.APPLICATION_JSON_VALUE)
//                 .body(postDto)
//             .when()
//                 .post("/users")
//             .then()
//                 .log().all()
//                 .statusCode(HttpStatus.BAD_REQUEST.value())
//                 .extract();
//         //@formatter:on
//
//         //then
//         int status = response.jsonPath().get("status");
//         List<String> list = response.jsonPath().getList("customFieldErrors");
//
//         assertThat(status).isEqualTo(400);
//         assertThat(list)
//             .extracting("field", "rejectedValue", "reason")
//             .containsAnyOf(
//                 tuple("phone", "010-11111-1111", "올바른 연락처 형식이 아닙니다."),
//                 tuple("phone", "", "올바른 연락처 형식이 아닙니다.")
//
//             );
//     }
//
//     private static Stream<Arguments> provideUserPostDtoWrongPwd() {
//         UserPostDto noSpecialSign = join("test@gmail.com", "sdfdfdf1", "010-1111-1111");
//         noSpecialSign.setPassword("sdfdfdf1");
//         UserPostDto noAlphabet = join("test@gmail.com", "1234567!", "010-1111-1111");
//         noAlphabet.setPassword("1234567!");
//         UserPostDto shortLength = join("test@gmail.com", "1234a7!", "010-1111-1111");
//         shortLength.setPassword("1234a7!");
//
//         return Stream.of(
//             Arguments.of(noSpecialSign),
//             Arguments.of(noAlphabet),
//             Arguments.of(shortLength)
//         );
//     }
//
//     @DisplayName("비밀번호 형식(공백 포함)을 지키지 않으면 예외를 던진다.")
//     @ParameterizedTest
//     @MethodSource("provideUserPostDtoWrongPwd")
//     void test4(UserPostDto postDto) throws Exception {
//             //@formatter:off
//             ExtractableResponse<Response> response =
//                 given()
//                     .log().all()
//                     .contentType(MediaType.APPLICATION_JSON_VALUE)
//                     .body(postDto)
//                 .when()
//                     .post("/users")
//                 .then()
//                     .log().all()
//                     .statusCode(HttpStatus.BAD_REQUEST.value())
//                     .extract();
//             //@formatter:on
//
//         //then
//         int status = response.jsonPath().get("status");
//         List<String> list = response.jsonPath().getList("customFieldErrors");
//
//         assertThat(status).isEqualTo(400);
//         assertThat(list)
//             .extracting("field", "rejectedValue", "reason")
//             .containsAnyOf(
//                 tuple("password", "sdfdfdf1", "비밀번호는 숫자+영문자+특수문자 조합으로 8자리 이상이어야 합니다."),
//                 tuple("password", "1234567!", "비밀번호는 숫자+영문자+특수문자 조합으로 8자리 이상이어야 합니다."),
//                 tuple("password", "1234a7!", "비밀번호는 숫자+영문자+특수문자 조합으로 8자리 이상이어야 합니다.")
//             );
//     }
//
//     private static Stream<Arguments> provideUserPostDtoWrongAddress() {
//         UserPostDto noCity = join("test@gmail.com", "sdfdfdf1", "010-1111-1111");
//         noCity.setCity("");
//         UserPostDto noAddress = join("test@gmail.com", "1234567!", "010-1111-1111");
//         noAddress.setDetailAddress("");
//
//
//         return Stream.of(
//             Arguments.of(noCity),
//             Arguments.of(noAddress)
//         );
//     }
//
//
//     @DisplayName("주소가 공백일 경우 예외를 던진다.")
//     @ParameterizedTest
//     @MethodSource("provideUserPostDtoWrongAddress")
//     void test6(UserPostDto postDto) throws Exception {
//         //@formatter:off
//         ExtractableResponse<Response> response =
//             given()
//                 .log().all()
//                 .contentType(MediaType.APPLICATION_JSON_VALUE)
//                 .body(postDto)
//             .when()
//                 .post("/users")
//             .then()
//                 .log().all()
//                 .statusCode(HttpStatus.BAD_REQUEST.value())
//                 .extract();
//         //@formatter:on
//
//         //then
//         int status = response.jsonPath().get("status");
//         List<String> list = response.jsonPath().getList("customFieldErrors");
//
//         assertThat(status).isEqualTo(400);
//         assertThat(list)
//             .extracting("field", "rejectedValue", "reason")
//             .containsAnyOf(
//                 tuple("city", "", "공백일 수 없습니다"),
//                 tuple("detailAddress", "", "공백일 수 없습니다")
//             );
//     }
//
//     private static UserPostDto join(String email, String displayName, String phone) {
//
//         String password = "sdfsdfe!1";
//         String city = "서울시 부평구 송도동";
//         String detailAddress = "101 번지";
//         String realName = "홍길동";
//
//         return UserPostDto.builder()
//             .detailAddress(detailAddress)
//             .city(city)
//             .email(email)
//             .phone(phone)
//             .realName(realName)
//             .password(password)
//             .displayName(displayName)
//             .build();
//     }
// }
