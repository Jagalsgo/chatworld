package com.jagalsgo.chatworld.repository;

import com.jagalsgo.chatworld.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAll();
}
