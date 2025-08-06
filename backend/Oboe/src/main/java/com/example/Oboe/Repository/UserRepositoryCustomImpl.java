package com.example.Oboe.Repository;


import com.example.Oboe.DTOs.UserSearchResultDTO;
import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Repository
public class UserRepositoryCustomImpl  implements  UserRepositoryCustom{
    @PersistenceContext
    private EntityManager em;


    @Override
    @Transactional
    public void deleteUserWithDependencies(UUID userId){
        em.createNativeQuery("DELETE FROM user_answers WHERE user_id = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM comments WHERE user_id = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM favorites WHERE user_id = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM message WHERE senderid = :id OR receiverid = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM notifications WHERE user_id = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM payment WHERE user_id = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM quiz_results WHERE user_id = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM reports WHERE user_id = :id").setParameter("id", userId).executeUpdate();



        em.createNativeQuery("DELETE FROM card_items WHERE set_id IN (SELECT set_id FROM flash_cards WHERE user_id = :id)")
                .setParameter("id", userId).executeUpdate();

        em.createNativeQuery("DELETE FROM questions WHERE quizzesid IN (SELECT quizzesid FROM quizzes WHERE user_id = :id)")
                .setParameter("id", userId).executeUpdate();

        em.createNativeQuery("DELETE FROM flash_cards WHERE user_id = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM quizzes WHERE user_id = :id").setParameter("id", userId).executeUpdate();

        em.createNativeQuery("DELETE FROM users WHERE user_id = :id").setParameter("id", userId).executeUpdate();

    }
}
