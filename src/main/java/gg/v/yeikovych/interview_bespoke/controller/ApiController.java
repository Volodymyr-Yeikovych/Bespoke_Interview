package gg.v.yeikovych.interview_bespoke.controller;

import gg.v.yeikovych.interview_bespoke.exception.NoTokenAvailableException;
import gg.v.yeikovych.interview_bespoke.model.Token;
import gg.v.yeikovych.interview_bespoke.service.ParsingService;
import gg.v.yeikovych.interview_bespoke.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bespoke")
public class ApiController {

    private final TokenService tokenService;
    private final ParsingService parsingService;

    @Autowired
    public ApiController(TokenService tokenService, ParsingService parsingService) {
        this.tokenService = tokenService;
        this.parsingService = parsingService;
    }

    @GetMapping("/new/{id}")
    public ResponseEntity<String> getTokenFromApi(@PathVariable String id) {
        var parsedId = parsingService.parseId(id);

        Token token;
        try {
            token = tokenService.assignOrGetToken(parsedId);
        } catch (NoTokenAvailableException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("429");
        }

        return ResponseEntity.status(HttpStatus.OK).body(token.getContent());
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<String> checkIfExists(@PathVariable String id) {
        var parsedId = parsingService.parseId(id);

        var hasValidToken = tokenService.hasValidTokenForId(parsedId);

        if (!hasValidToken) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false+"");
        return ResponseEntity.status(HttpStatus.OK).body(true+"");
    }
}
