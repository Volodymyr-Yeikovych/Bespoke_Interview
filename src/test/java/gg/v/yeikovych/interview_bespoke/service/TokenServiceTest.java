package gg.v.yeikovych.interview_bespoke.service;

import gg.v.yeikovych.interview_bespoke.exception.NoTokenAvailableException;
import gg.v.yeikovych.interview_bespoke.model.IdHolder;
import gg.v.yeikovych.interview_bespoke.model.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TokenServiceTest {


    @InjectMocks
    private TokenService tokenService;
    @Mock
    private TokenGenerationService tokenGenerationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenService.generateTokens();
    }

    @Test
    public void shouldGeneratePoolSizeNumberOfTokens() {
        verify(tokenGenerationService, times(tokenService.getPoolSize())).generateToken();
    }

    @Test
    public void shouldAssignValidTokenForUser() throws NoTokenAvailableException {
        IdHolder idHolder = new IdHolder("a");
        Token token = tokenService.assignOrGetToken(idHolder);

        assertThat(token).isNotNull();
        assertThat(tokenService.hasValidTokenForId(idHolder)).isTrue();
    }

    @Test
    public void shouldAssignAllTokensAndReturnNoTokenAvailableForAnyNextOne() {
        IdHolder idHolder1 = new IdHolder("a");
        IdHolder idHolder2 = new IdHolder("a1");
        IdHolder idHolder3 = new IdHolder("a2");

        tokenService.assignOrGetToken(idHolder1);
        tokenService.assignOrGetToken(idHolder2);

        assertThrows(NoTokenAvailableException.class, () -> tokenService.assignOrGetToken(idHolder3));
    }

    @Test
    public void shouldReassignExpiredTokens() throws NoTokenAvailableException {
        IdHolder idHolder = new IdHolder("a");
        Token token = tokenService.assignOrGetToken(idHolder);

        token.setTimeExpires(token.getTimeIssued().minusSeconds(1));

        long reassigned = tokenService.reassignExpiredTokens();

        assertThat(reassigned).isEqualTo(1);
        assertThat(tokenService.hasValidTokenForId(idHolder)).isFalse();
    }

    @Test
    public void shouldReturnTrueForValidTokensAndFalseForInvalidTokensAndNoTokensAssigned() throws NoTokenAvailableException {
        IdHolder idWithToken = new IdHolder("a");
        IdHolder idWithoutToken = new IdHolder("a1");

        Token token = tokenService.assignOrGetToken(idWithToken);

        assertThat(tokenService.hasValidTokenForId(idWithToken)).isTrue();

        assertThat(tokenService.hasValidTokenForId(idWithoutToken)).isFalse();

        token.setTimeExpires(token.getTimeIssued().minusSeconds(1));

        assertThat(tokenService.hasValidTokenForId(idWithToken)).isFalse();
    }

    @Test
    public void shouldReturnTheSameTokenIdForTheSameUserIfTokenIsNotExpired() {
        IdHolder idWithToken = new IdHolder("a");

        Token token1 = tokenService.assignOrGetToken(idWithToken);
        Token token2 = tokenService.assignOrGetToken(idWithToken);

        assertThat(token1.isExpired()).isFalse();
        assertThat(token2.isExpired()).isFalse();
        assertThat(token1).isEqualTo(token2);
    }
}
