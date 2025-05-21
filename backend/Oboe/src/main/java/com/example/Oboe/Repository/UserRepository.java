package com.example.Oboe.Repository;

import com.example.Oboe.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserName(String userName);
    void deleteByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
