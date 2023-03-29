package com.jagalsgo.chatworld.Service;

import com.jagalsgo.chatworld.security.TokenInfo;
import com.jagalsgo.chatworld.entity.User;
import com.jagalsgo.chatworld.repository.UserRepository;
import com.jagalsgo.chatworld.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenInfo login(User user) {

        User validUser = userRepository.findByUserId(user.getUserId());
        if (!passwordEncoder.matches(user.getPassword(), validUser.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        System.out.println("로그인");
        return jwtTokenProvider.createToken(validUser.getUserId(), validUser.getRole());

    }

    public void join(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("MEMBER");
        System.out.println(user.getPassword());
        userRepository.save(user);
    }

}
