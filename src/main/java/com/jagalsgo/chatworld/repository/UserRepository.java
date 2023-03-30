package com.jagalsgo.chatworld.repository;

import com.jagalsgo.chatworld.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(String userId);
    boolean existsByUserId(String userId);
    boolean existsByNickname(String nickname);

}
