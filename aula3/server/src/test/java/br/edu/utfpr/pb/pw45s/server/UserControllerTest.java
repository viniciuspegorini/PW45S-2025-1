package br.edu.utfpr.pb.pw45s.server;

import br.edu.utfpr.pb.pw45s.server.error.ApiError;
import br.edu.utfpr.pb.pw45s.server.model.User;
import br.edu.utfpr.pb.pw45s.server.repository.UserRepository;
import br.edu.utfpr.pb.pw45s.server.utils.GenericResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    UserRepository userRepository;

    @BeforeEach()
    public void cleanup() {
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    // methodName_condition_expectedBehaviour
    @Test
    public void postUser_whenUserIsValid_receiveOk() {
        User user = createValidUser();
        ResponseEntity<Object> response =
                testRestTemplate.postForEntity("/users", user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postUser_whenUserIsValid_userSavedToDatabase() {
        User user = createValidUser();

        ResponseEntity<Object> response =
                testRestTemplate.postForEntity("/users", user, Object.class);
        assertThat( userRepository.count() ).isEqualTo(1);
    }

    @Test
    public void postUser_whenUserIsValid_receiveSuccessMessage() {
        User user = createValidUser();

        ResponseEntity<GenericResponse> response =
                testRestTemplate.postForEntity("/users", user, GenericResponse.class);
        assertThat( response.getBody().getMessage() ).isNotNull();
    }

    @Test
    @DisplayName("Post user when User is valid password is hashed in database")
    public void postUser_whenUserIsValid_passwordIsHashedInDatabase() {
        User user = createValidUser();
        testRestTemplate.postForEntity("/users", user, Object.class);

        List<User> userList = userRepository.findAll();
        User userDB = userList.get(0);
        assertThat(userDB.getPassword()).isNotEqualTo(user.getPassword());
    }

    @Test
    public void postUser_whenUserHasNullUsername_receiveBadRequest() {
        User user = createValidUser();
        user.setUsername(null);
        ResponseEntity<Object> response =
                testRestTemplate.postForEntity("/users", user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullPassword_receiveBadRequest() {
        User user = createValidUser();
        user.setPassword(null);
        ResponseEntity<Object> response =
                testRestTemplate.postForEntity("/users", user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserWithSizeLessThanRequired_receiveBadRequest() {
        User user = createValidUser();
        user.setUsername("abc");
        ResponseEntity<Object> response =
                testRestTemplate.postForEntity("/users", user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUsernameSizeExceedsLengthLimit_receiveBadRequest() {
        User user = createValidUser();
        user.setUsername(
                IntStream.rangeClosed(1, 256).mapToObj(x -> "x")
                        .collect(Collectors.joining())
        );
        ResponseEntity<Object> response =
                testRestTemplate.postForEntity("/users", user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenPasswordPatternNotMatch_receiveBadRequest() {
        User user = createValidUser();
        user.setPassword("password");
        ResponseEntity<Object> response =
                testRestTemplate.postForEntity("/users", user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserIsInvalid_receiveApiError() {
        User user = new User();
        ResponseEntity<ApiError> response =
            testRestTemplate.postForEntity("/users", user, ApiError.class);

        assertThat(response.getBody().getUrl()).isEqualTo("/users");
    }

    @Test
    public void postUser_whenUserIsInvalid_receiveApiErrorWithValidationErrors() {
        User user = new User();
        ResponseEntity<ApiError> response =
                testRestTemplate.postForEntity("/users", user, ApiError.class);

        assertThat(response.getBody().getValidationErrors().size()
                ).isEqualTo(3);
    }

    @Test
    public void postUser_whenUserIsInvalid_receiveMessageOfNullUsername() {
        User user = new User();
        ResponseEntity<ApiError> response =
                testRestTemplate.postForEntity("/users", user, ApiError.class);

        Map<String, String> validationErrors = response.getBody().getValidationErrors();

        assertThat(validationErrors.get("username"))
                .isEqualTo("must not be empty");
    }

    @Test
    public void postUser_whenAnotherUserHasSameUsername_receiveBadRequest() {
        userRepository.save(createValidUser());
        User user = createValidUser();
        ResponseEntity<Object> response =
                testRestTemplate.postForEntity("/users", user, Object.class);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void patchUser_whenUserIsValid_receiveMessage() {
        User user = createValidUser();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> entity = new HttpEntity<User>(user,
                headers);


        ResponseEntity<GenericResponse> response = testRestTemplate.exchange(
                "/users?_method=patch", HttpMethod.POST, entity, GenericResponse.class);
        assertThat( response.getBody().getMessage() ).isNotNull();

    }

    private User createValidUser() {
        User user = new User();
        user.setUsername("test-user");
        user.setDisplayName("test-dislpay");
        user.setPassword("P4ssword");

        return user;
    }
}
