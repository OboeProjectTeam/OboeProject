package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
    public interface FavoritesRepository extends JpaRepository<Favorites, UUID> {
    @Query("SELECT f FROM Favorites f WHERE f.user.user_id = :userId")
    List<Favorites> findByUserId(@Param("userId") UUID userId);


    @Query("SELECT f FROM Favorites f WHERE f.user.user_id = :userId AND f.kanji.kanjiId = :kanjiId")
    Optional<Favorites> findFavoriteKanji(@Param("userId") UUID userId, @Param("kanjiId") UUID kanjiId);


    @Query("SELECT f FROM Favorites f WHERE f.user.user_id = :userId AND f.gramma.grammaID = :grammaid")
    Optional<Favorites> findFavoriteGramma(@Param("userId") UUID userId, @Param("grammaid") UUID grammaId);

    @Query("SELECT f FROM Favorites f WHERE f.user.user_id = :userId AND f.vocabulary.vocalbId = :vocabId")
    Optional<Favorites> findFavoriteVocabulary(@Param("userId") UUID userId, @Param("vocabId") UUID vocabId);


    @Query("SELECT f FROM Favorites f WHERE f.user.user_id = :userId AND f.sentence.sample_sentence_id = :sentenceId")
    Optional<Favorites> findFavoriteSentence(@Param("userId") UUID userId, @Param("sentenceId") UUID sentenceId);


}
