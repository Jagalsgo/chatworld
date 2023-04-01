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
        if(validUser == null){
            throw new IllegalArgumentException("잘못된 아이디입니다.");
        }
        if (!passwordEncoder.matches(user.getPassword(), validUser.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return jwtTokenProvider.createToken(validUser.getUserId(), validUser.getRole());
    }

    public boolean join(User user) throws Exception {
        if(user.getUserId() == null || user.getPassword() == null || user.getNickname() == null){
            throw new Exception("Name and password are required");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("MEMBER");
        User resultUser = userRepository.save(user);
        if(resultUser != null){
            return true;
        }else {
            return false;
        }
    }

    public boolean isUserIdDuplication(String userId){
        return userRepository.existsByUserId(userId);
    }

    public boolean isNicknameDuplication(String nickname){
        return userRepository.existsByNickname(nickname);
    }

}
