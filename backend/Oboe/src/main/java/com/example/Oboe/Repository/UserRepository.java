package com.example.Oboe.Repository;

import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUserNameAndAuthProvider(String userName, AuthProvider authProvider);

    boolean existsByUserNameAndAuthProvider(String userName, AuthProvider authProvider);
    List<User> findAllByUserName(String userName);
    Optional<User> findByUserName(String userName);


}
