package gg.v.yeikovych.interview_bespoke.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenGenerationService {

    public String generateToken() {
        return UUID.randomUUID().toString();
    }
}
