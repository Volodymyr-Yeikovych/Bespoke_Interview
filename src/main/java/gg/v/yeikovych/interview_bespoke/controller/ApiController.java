package gg.v.yeikovych.interview_bespoke.controller;

import gg.v.yeikovych.interview_bespoke.model.IdHolder;
import gg.v.yeikovych.interview_bespoke.model.Token;
import gg.v.yeikovych.interview_bespoke.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tokens/")
public class ApiController {

    private final TokenService tokenService;
    @Autowired
    public ApiController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<String> getTokenFromApi(@PathVariable String userId) {
        IdHolder parsedId = new IdHolder(userId);

        Token token;
        try {
            token = tokenService.assignOrGetToken(parsedId);
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("No Tokens available for the given moment.");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, "text/plain")
                .body(token.getContent());
    }

    @GetMapping("/{userId}/{tokenId}")
    public ResponseEntity<String> checkIfExists(@PathVariable String userId, @PathVariable String tokenId) {
        IdHolder parsedId = new IdHolder(userId);

        boolean hasValidToken = tokenService.hasTokenForId(parsedId, tokenId);

        if (!hasValidToken)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Token {" + tokenId + "} for UserId {" + userId + "} was not found.");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Token {" + tokenId + "} exists for UserId {" + userId + "}, and is valid.");
    }
}
