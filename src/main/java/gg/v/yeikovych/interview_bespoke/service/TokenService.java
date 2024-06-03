package gg.v.yeikovych.interview_bespoke.service;

import gg.v.yeikovych.interview_bespoke.model.IdHolder;
import gg.v.yeikovych.interview_bespoke.model.Token;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TokenService {

    private static long POOL_SIZE = 2;
    private static long EXPIRY_TIME_SECONDS = 20;
    private final List<Token> availableTokens = new CopyOnWriteArrayList<>();
    private final Map<IdHolder, Token> usersAndTokens = new ConcurrentHashMap<>();
    private final TokenGenerationService tokenGenerationService;
    private boolean wasTokensGenerated = false;

    @Autowired
    public TokenService(TokenGenerationService tokenGenerationService) {
        this.tokenGenerationService = tokenGenerationService;
    }

    @PostConstruct
    public void init() {
        init(POOL_SIZE, EXPIRY_TIME_SECONDS);
    }

    public void init (long poolSize, long timeExpires) {
        if (wasTokensGenerated) return;

        POOL_SIZE = poolSize;
        EXPIRY_TIME_SECONDS = timeExpires;

        availableTokens.addAll(tokenGenerationService.generateTokens(POOL_SIZE, EXPIRY_TIME_SECONDS));
        wasTokensGenerated = true;
    }

    public Token assignOrGetToken(IdHolder idHolder) throws IllegalStateException {
        Token token = usersAndTokens.get(idHolder);

        if (token != null && !token.isExpired()) {
            return token;
        } else {
            token = assignTokenTo(idHolder);
        }

        return token;
    }

    public boolean hasTokenForId(IdHolder id, String tokenId) {
        Token token = usersAndTokens.get(id);

        if (token == null || token.isExpired() || !token.getContent().equals(tokenId)) return false;

        return true;
    }

    private void addTokenToAvailable(Token token) {
        availableTokens.add(token);
    }

    private Token assignTokenTo(IdHolder idHolder) throws IllegalStateException{
        if (availableTokens.isEmpty()) {
            var wasReassigned = reassignExpiredTokens();
            if (!wasReassigned) throw new IllegalStateException("Pool has no tokens left.");
        }
        Token token = availableTokens.removeFirst();
        usersAndTokens.put(idHolder, token);
        return token;
    }

    private boolean reassignExpiredTokens() {
        boolean wasAnyTokenReassigned = false;

        var iterator = usersAndTokens.entrySet().iterator();

        while (iterator.hasNext()) {
            var entry = iterator.next();

            IdHolder k = entry.getKey();
            Token v = entry.getValue();

            if (v.isExpired()) {
                resetExpiryTimer(v);

                iterator.remove();

                addTokenToAvailable(v);
                wasAnyTokenReassigned = true;
            }
        }
        return wasAnyTokenReassigned;
    }

    private void resetExpiryTimer(Token token) {
        LocalDateTime timeIssued = LocalDateTime.now();
        LocalDateTime timeExpires = timeIssued.plusSeconds(EXPIRY_TIME_SECONDS);

        token.setTimeIssued(timeIssued);
        token.setTimeExpires(timeExpires);
    }

}
