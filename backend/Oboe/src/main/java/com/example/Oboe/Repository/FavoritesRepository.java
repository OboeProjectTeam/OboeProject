package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

    public interface FavoritesRepository extends JpaRepository<Favorites, UUID> {
    @Query("SELECT f FROM Favorites f WHERE f.user.user_id = :userId")
    List<Favorites> findByUserId(@Param("userId") UUID userId);



}
