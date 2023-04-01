package com.jagalsgo.chatworld.controller;

import com.jagalsgo.chatworld.security.JwtTokenProvider;
import com.jagalsgo.chatworld.security.TokenInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login2")
    public String login2(HttpServletRequest request, HttpServletResponse response) {
        return "index";
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody TokenInfo tokenInfo) {
        // JWT 검증 및 유효성 체크
        if (tokenInfo.getAccessToken() != null && jwtTokenProvider.validateToken(tokenInfo.getAccessToken())) {
            // JWT 를 무효화
            jwtTokenProvider.invalidateToken(tokenInfo);
            return ResponseEntity.ok("로그아웃 되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("로그인 상태가 아닙니다.");
        }
    }

    // 로그인 유무 검사
    @GetMapping("/checkLogin")
    public ResponseEntity<Boolean> checkLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()){
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

}
