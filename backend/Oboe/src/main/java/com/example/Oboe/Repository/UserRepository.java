package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.UserDTOs;
import com.example.Oboe.DTOs.UserSearchResultDTO;
import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {


    @Query("SELECT u FROM User u WHERE u.userName = :userName AND u.authProvider = :authProvider")
    List<User> findAllByUserNameAndAuthProvider(@Param("userName") String userName,
                                                @Param("authProvider") AuthProvider authProvider);

    @Query(value = "SELECT * FROM users WHERE user_id = :userId", nativeQuery = true)
    Optional<User> findByUser_id(@Param("userId") UUID userId);

    boolean existsByUserNameAndAuthProvider(String userName, AuthProvider authProvider);

    List<User> findAllByUserName(String userName);

    @Query("SELECT u FROM User u WHERE u.user_id IN :ids")
    List<User> findByUserIdIn(@Param("ids") List<UUID> ids);

    @Query("SELECT COUNT(u) FROM User u")
    Long countAllUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE FUNCTION('MONTH', u.create_at) = FUNCTION('MONTH', CURRENT_DATE)")
    Long countUsersThisMonth();

    @Query("SELECT u.userName, u.create_at FROM User u ORDER BY u.create_at DESC")
    List<Object[]> findLatestRegisteredUser();

    @Query("SELECT new com.example.Oboe.DTOs.UserSearchResultDTO(u.user_id, u.userName, COUNT(f)) " +
            "FROM User u LEFT JOIN FlashCards f ON f.user.user_id = u.user_id " +
            "WHERE LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "GROUP BY u.user_id, u.userName")
    List<UserSearchResultDTO> searchUsersWithFlashcardCount(@Param("keyword") String keyword);

    @Query("SELECT new com.example.Oboe.DTOs.UserDTOs(u.user_id, u.userName, u.avatarUrl) " +
            "FROM User u WHERE LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<UserDTOs> searchUsersByKeyword(@Param("keyword") String keyword);
}
