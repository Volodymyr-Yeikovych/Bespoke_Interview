package gg.v.yeikovych.interview_bespoke.service;

import gg.v.yeikovych.interview_bespoke.exception.NoTokenAvailableException;
import gg.v.yeikovych.interview_bespoke.model.IdHolder;
import gg.v.yeikovych.interview_bespoke.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TokenService {

    private final static long POOL_SIZE = 2;
    private final static long DEFAULT_EXPIRY_TIME_SECONDS = 20;
    private final List<Token> availableTokens = new ArrayList<>();
    private final Map<IdHolder, Token> usersAndTokens = new HashMap<>();
    private final TokenGenerationService tokenGenerationService;

    @Autowired
    public TokenService(TokenGenerationService tokenGenerationService) {
        this.tokenGenerationService = tokenGenerationService;
    }

    public void generateTokens() {
        for (int i = 0; i < POOL_SIZE; i++) {
            var token = generateToken();
            initToken(token);
        }
    }

    public Token assignOrGetToken(IdHolder idHolder) throws NoTokenAvailableException {
        Token token = usersAndTokens.get(idHolder);

        if (token != null && !token.isExpired()) {
            return token;
        }

        if (token == null) {
            token = assignTokenTo(idHolder);
        }

        if (token.isExpired()) {
            token = assignTokenTo(idHolder);
        }

        return token;
    }

    private Token assignTokenTo(IdHolder idHolder) throws NoTokenAvailableException{
        if (availableTokens.isEmpty()) {
            var reassigned = reassignExpiredTokens();
            if (reassigned == 0) throw new NoTokenAvailableException("Pool has no tokens left.");
        }
        Token token = availableTokens.removeFirst();
        usersAndTokens.put(idHolder, token);
        return token;
    }

    public long reassignExpiredTokens() {
        long countRevalidated = 0;
        var toIterate = new HashMap<>(usersAndTokens);
        for (Map.Entry<IdHolder, Token> entry : toIterate.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();

            if (v.isExpired()) {
                resetExpiryTimer(v);
                removeUserFromTokenHolders(k);
                addTokenToAvailable(v);
                countRevalidated++;
            }
        }
        return countRevalidated;
    }

    public boolean hasValidTokenForId(IdHolder id) {
        var token = usersAndTokens.get(id);

        if (token == null) return false;
        if (token.isExpired()) return false;

        return true;
    }

    public int getPoolSize() {
        return (int) POOL_SIZE;
    }

    private void addTokenToAvailable(Token token) {
        availableTokens.add(token);
    }

    private void removeUserFromTokenHolders(IdHolder idHolder) {
        usersAndTokens.remove(idHolder);
    }

    private Token generateToken() {
        Token token = new Token();
        token.setContent(tokenGenerationService.generateToken());

        resetExpiryTimer(token);

        return token;
    }

    private void initToken(Token token) {
        availableTokens.add(token);
    }

    private void resetExpiryTimer(Token token) {
        var timeGiven = LocalDateTime.now();
        var timeExpires = timeGiven.plusSeconds(DEFAULT_EXPIRY_TIME_SECONDS);

        token.setTimeIssued(timeGiven);
        token.setTimeExpires(timeExpires);
    }

}
