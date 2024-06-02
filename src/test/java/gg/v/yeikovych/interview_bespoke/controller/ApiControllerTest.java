package gg.v.yeikovych.interview_bespoke.controller;

import gg.v.yeikovych.interview_bespoke.exception.NoTokenAvailableException;
import gg.v.yeikovych.interview_bespoke.model.IdHolder;
import gg.v.yeikovych.interview_bespoke.model.Token;
import gg.v.yeikovych.interview_bespoke.service.ParsingService;
import gg.v.yeikovych.interview_bespoke.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

public class ApiControllerTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private ParsingService parsingService;

    @InjectMocks
    private ApiController apiController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTokenFromApi() throws NoTokenAvailableException {
        String id = "a";
        String stubToken = "imHappyToken";
        IdHolder holder = new IdHolder(id);

        when(parsingService.parseId(id)).thenReturn(holder);

        Token token = new Token();
        token.setContent(stubToken);
        when(tokenService.assignOrGetToken(holder)).thenReturn(token);

        ResponseEntity<String> responseEntity = apiController.getTokenFromApi(id);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(stubToken);
    }

    @Test
    public void testGetTokenFromApi_NoTokenAvailable() throws NoTokenAvailableException {
        String id = "a";
        String expected = "429";
        IdHolder holder = new IdHolder(id);

        when(parsingService.parseId(id)).thenReturn(holder);

        when(tokenService.assignOrGetToken(holder)).thenThrow(new NoTokenAvailableException("No token available"));

        ResponseEntity<String> responseEntity = apiController.getTokenFromApi(id);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(responseEntity.getBody()).isEqualTo(expected);
    }

    @Test
    public void testCheckIfExists_WithValidToken() {
        String id = "a";
        String expected = "true";
        IdHolder holder = new IdHolder(id);

        when(parsingService.parseId(id)).thenReturn(holder);

        when(tokenService.hasValidTokenForId(holder)).thenReturn(true);

        ResponseEntity<String> responseEntity = apiController.checkIfExists(id);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expected);
    }

    @Test
    public void testCheckIfExists_WithInvalidToken() {
        String id = "a";
        String expected = "false";
        IdHolder holder = new IdHolder(id);

        when(parsingService.parseId(id)).thenReturn(holder);

        when(tokenService.hasValidTokenForId(holder)).thenReturn(false);

        ResponseEntity<String> responseEntity = apiController.checkIfExists(id);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isEqualTo(expected);
    }
}
