package com.jagalsgo.chatworld.interceptor;

import com.jagalsgo.chatworld.entity.Team;
import com.jagalsgo.chatworld.repository.TeamRepository;
import com.jagalsgo.chatworld.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NavbarInterceptor implements HandlerInterceptor {
    private final TeamRepository teamRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        List<Team> teams = teamRepository.findAll();
        List<Team> team1 = new ArrayList<>();
        List<Team> team2 = new ArrayList<>();

        for (Team team : teams) {
            if (team.getLeague() == 1) {
                team1.add(team);
            } else if (team.getLeague() == 2) {
                team2.add(team);
            }
        }

        request.setAttribute("team1", team1);
        request.setAttribute("team2", team2);
        return true;
    }
}
