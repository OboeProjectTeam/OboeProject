package com.example.Oboe.DTOs;

import org.springframework.data.domain.Page;

import java.util.List;

public class SearchResultWrapperDTO {
    public SearchResultWrapperDTO() {

    }

    public Page<FlashcardSearchResultDTO> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(Page<FlashcardSearchResultDTO> flashcards) {
        this.flashcards = flashcards;
    }

    public Page<QuizSearchResultDTO> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(Page<QuizSearchResultDTO> quizzes) {
        this.quizzes = quizzes;
    }

    public Page<UserSearchResultDTO> getUsers() {
        return users;
    }

    public void setUsers(Page<UserSearchResultDTO> users) {
        this.users = users;
    }

    private Page<FlashcardSearchResultDTO> flashcards;
    private Page<QuizSearchResultDTO> quizzes;
    private Page<UserSearchResultDTO> users;



    public SearchResultWrapperDTO(Page<FlashcardSearchResultDTO> flashcards,
                                  Page<QuizSearchResultDTO> quizzes,
                                  Page<UserSearchResultDTO> users) {
        this.flashcards = flashcards;
        this.quizzes = quizzes;
        this.users = users;
    }
}