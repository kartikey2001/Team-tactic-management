package org.example.team_tactic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class TeamTacticApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamTacticApplication.class, args);
    }
}
