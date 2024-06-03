package gg.v.yeikovych.interview_bespoke.service;

import gg.v.yeikovych.interview_bespoke.model.Token;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenGenerationServiceTest {

    @InjectMocks
    private TokenGenerationService tokenGenerationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldGenerateCorrectTokens() {
        long poolSize = 5;
        long expirySeconds = 3600;
        LocalDateTime now = LocalDateTime.now();

        List<Token> tokens = tokenGenerationService.generateTokens(poolSize, expirySeconds);

        assertThat(poolSize).isEqualTo(tokens.size());

        for (var t : tokens) {
            assertThat(now.getSecond()).isCloseTo(t.getTimeIssued().getSecond(), Percentage.withPercentage(5));
            assertThat(now.plusSeconds(expirySeconds).getSecond()).isCloseTo(t.getTimeExpires().getSecond(), Percentage.withPercentage(5));
        }
    }
}
