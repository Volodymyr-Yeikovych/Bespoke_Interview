package gg.v.yeikovych.interview_bespoke.controller;

import gg.v.yeikovych.interview_bespoke.model.IdHolder;
import gg.v.yeikovych.interview_bespoke.model.Token;
import gg.v.yeikovych.interview_bespoke.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

public class ApiControllerTest {

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private ApiController apiController;
    private Token token;
    private IdHolder user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        String tokenContent = "imHappyToken";
        LocalDateTime timeIssued = LocalDateTime.now();
        LocalDateTime timeExpires = timeIssued.plusHours(1);

        token = new Token(tokenContent, timeIssued, timeExpires);
        String userId = "aaa";
        user = new IdHolder(userId);
    }

    @Test
    public void shouldReturnOkForCorrectToken() throws IllegalStateException {
        when(tokenService.assignOrGetToken(user)).thenReturn(token);

        ResponseEntity<String> responseEntity = apiController.getTokenFromApi(user.getUserId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(token.getContent());
    }

    @Test
    public void shouldReturnTooManyRequestsWhenNoTokensAvailable() throws IllegalStateException {
        String expectedMessage = "No Tokens available for the given moment.";

        when(tokenService.assignOrGetToken(user)).thenThrow(new IllegalStateException("No token available"));

        ResponseEntity<String> responseEntity = apiController.getTokenFromApi(user.getUserId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(responseEntity.getBody()).isEqualTo(expectedMessage);
    }

    @Test
    public void shouldReturnOkForExistingUserAndValidToken() {
        String content = token.getContent();
        String id = user.getUserId();
        String expectedMessage = "Token {" + content + "} exists for UserId {" + id + "}, and is valid.";

        when(tokenService.hasTokenForId(user, content)).thenReturn(true);

        ResponseEntity<String> responseEntity = apiController.checkIfExists(id, content);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expectedMessage);
    }

    @Test
    public void shouldReturnNotFoundForNonExistingUserOrInValidToken() {
        String content = token.getContent();
        String id = user.getUserId();
        String expectedMessage = "Token {" + content + "} for UserId {" + id + "} was not found.";

        when(tokenService.hasTokenForId(user, content)).thenReturn(false);

        ResponseEntity<String> responseEntity = apiController.checkIfExists(id, content);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isEqualTo(expectedMessage);
    }
}
