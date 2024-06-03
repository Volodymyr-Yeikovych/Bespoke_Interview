package gg.v.yeikovych.interview_bespoke.service;

import gg.v.yeikovych.interview_bespoke.model.IdHolder;
import gg.v.yeikovych.interview_bespoke.model.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TokenServiceTest {


    @InjectMocks
    private TokenService tokenService;
    @Mock
    private TokenGenerationService tokenGenerationService;
    private static final long TEST_POOL_SIZE = 2;
    private static final long TEST_TIME_EXPIRES = 60;
    private IdHolder user1;
    private Token token1;
    private Token token2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        token1 = new Token(UUID.randomUUID().toString(), LocalDateTime.now(), LocalDateTime.now().plusSeconds(TEST_TIME_EXPIRES));
        token2 = new Token(UUID.randomUUID().toString(), LocalDateTime.now(), LocalDateTime.now().plusSeconds(TEST_TIME_EXPIRES));

        List<Token> list = new ArrayList<>();
        list.add(token1);
        list.add(token2);

        when(tokenGenerationService.generateTokens(TEST_POOL_SIZE, TEST_TIME_EXPIRES)).thenReturn(list);

        tokenService.init(TEST_POOL_SIZE, TEST_TIME_EXPIRES);

        user1 = new IdHolder("a1");
    }

    @Test
    public void shouldAssignValidTokenForUser() throws IllegalStateException {
        Token token = tokenService.assignOrGetToken(user1);

        assertThat(token).isNotNull();
        assertThat(tokenService.hasTokenForId(user1, token.getContent())).isTrue();
    }

    @Test
    public void shouldAssignAllTokensAndReturnNoTokenAvailableForAnyNextOne() {
        IdHolder user2 = new IdHolder("a2");
        IdHolder user3 = new IdHolder("a3");

        tokenService.assignOrGetToken(user1);
        tokenService.assignOrGetToken(user2);

        assertThrows(IllegalStateException.class, () -> tokenService.assignOrGetToken(user3));
    }

    @Test
    public void shouldReturnTrueForValidTokensAndFalseForInvalidTokensAndNoTokensAssigned() throws IllegalStateException {
        IdHolder idWithoutToken = new IdHolder("a2");

        Token token = tokenService.assignOrGetToken(user1);
        String content = token.getContent();

        assertThat(tokenService.hasTokenForId(user1, content)).isTrue();

        assertThat(tokenService.hasTokenForId(idWithoutToken, content)).isFalse();

        token.setTimeExpires(token.getTimeIssued().minusSeconds(1));

        assertThat(tokenService.hasTokenForId(user1, content)).isFalse();
    }

    @Test
    public void shouldReturnTheSameTokenIdForTheSameUserIfTokenIsNotExpired() {
        Token token1 = tokenService.assignOrGetToken(user1);
        Token token2 = tokenService.assignOrGetToken(user1);

        assertThat(token1.isExpired()).isFalse();
        assertThat(token2.isExpired()).isFalse();
        assertThat(token1).isEqualTo(token2);
    }
}
