package gg.v.yeikovych.interview_bespoke;

import gg.v.yeikovych.interview_bespoke.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BespokeInterviewApplication {

    public final TokenService tokenService;

    @Autowired
    public BespokeInterviewApplication(TokenService tokenService) {
        this.tokenService = tokenService;
    }


    public static void main(String[] args) {
        SpringApplication.run(BespokeInterviewApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner run() {
//        tokenService.init();
//        return args -> {
//        };
//    }
}
