package com.jagalsgo.chatworld.controller;

import com.jagalsgo.chatworld.Service.UserService;
import com.jagalsgo.chatworld.security.TokenInfo;
import com.jagalsgo.chatworld.entity.User;
import com.jagalsgo.chatworld.repository.UserRepository;
import com.jagalsgo.chatworld.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/loginPage")
    public String loginPage() {
        return "loginPage";
    }

    @PostMapping("/login")
    public ResponseEntity<TokenInfo> login(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("로그인 컨트롤러");
        // 로그인 처리
        TokenInfo tokenInfo = userService.login(user);
        // TokenInfo 객체를 JSON 형태로 Response Body 에 포함시켜 응답
        return ResponseEntity.ok(tokenInfo);
    }

    @PostMapping("/login2")
    public String login2(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("로그인2 컨트롤러");
        return "index";
    }

    @GetMapping("/joinPage")
    public String joinPage() {
        return "joinPage";
    }

    @PostMapping("/join")
    public String join(@RequestBody User user) {
        userService.join(user);
        return "joinPage";
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<TokenInfo> refreshToken(@RequestBody Map<String, String> inputData) {
        System.out.println("refreshToken 컨트롤러");

        // access 토큰을 가져오기 위한 refresh 토큰
        String refreshToken = inputData.get("refreshToken");

        // userId와 role 을 추출
        String userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
        String role = jwtTokenProvider.getRoleFromRefreshToken(refreshToken);

        // 새로운 access 토큰을 생성
        TokenInfo tokenInfo = jwtTokenProvider.createToken(userId, role);

        return ResponseEntity.ok(tokenInfo);
    }

}
