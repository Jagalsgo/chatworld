package com.jagalsgo.chatworld.controller;

import com.jagalsgo.chatworld.Service.UserService;
import com.jagalsgo.chatworld.entity.User;
import com.jagalsgo.chatworld.repository.UserRepository;
import com.jagalsgo.chatworld.security.JwtTokenProvider;
import com.jagalsgo.chatworld.security.TokenInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
        try{
            // 로그인 처리
            TokenInfo tokenInfo = userService.login(user);
            // TokenInfo 객체를 JSON 형태로 Response Body 에 포함시켜 응답
            return ResponseEntity.ok(tokenInfo);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/joinPage")
    public String joinPage() {
        return "joinPage";
    }

    @PostMapping("/join")
    @ResponseBody
    public boolean join(@RequestBody User user) {
        try {
            boolean result = userService.join(user);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 아이디 중복 검사
    @GetMapping("/checkUserIdDuplication")
    @ResponseBody
    public Map<String, Boolean> checkUserIdDuplication(@RequestParam String userId) {
        boolean isDuplicated = userService.isUserIdDuplication(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("duplicated", isDuplicated);
        return response;
    }

    // 닉네임 중복 검사
    @GetMapping("/checkNicknameDuplication")
    @ResponseBody
    public Map<String, Boolean> checkNicknameDuplication(@RequestParam String nickname) {
        boolean isDuplicated = userService.isNicknameDuplication(nickname);
        Map<String, Boolean> response = new HashMap<>();
        response.put("duplicated", isDuplicated);
        return response;
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
