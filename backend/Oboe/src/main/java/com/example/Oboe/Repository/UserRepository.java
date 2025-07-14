package com.example.Oboe.Repository;

import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {


    @Query("SELECT u FROM User u WHERE u.userName = :userName AND u.authProvider = :authProvider")
    List<User> findAllByUserNameAndAuthProvider(@Param("userName") String userName,
                                                @Param("authProvider") AuthProvider authProvider);

    boolean existsByUserNameAndAuthProvider(String userName, AuthProvider authProvider);

    List<User> findAllByUserName(String userName);

    @Query("SELECT u FROM User u WHERE u.user_id IN :ids")
    List<User> findByUserIdIn(@Param("ids") List<UUID> ids);
}
