package gg.v.yeikovych.interview_bespoke.service;

import gg.v.yeikovych.interview_bespoke.model.Token;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TokenGenerationService {

    private Token generateToken(long expirySeconds) {
        String content = UUID.randomUUID().toString();
        LocalDateTime timeIssued = LocalDateTime.now();
        LocalDateTime timeExpires = timeIssued.plusSeconds(expirySeconds);

        return new Token(content, timeIssued, timeExpires);
    }

    public List<Token> generateTokens(long poolSize, long expirySeconds) {
        List<Token> availableTokens = new ArrayList<>();
        for (int i = 0; i < poolSize; i++) {
            Token token = generateToken(expirySeconds);
            availableTokens.add(token);
        }
        return availableTokens;
    }
}
