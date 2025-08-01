package com.example.Oboe.DTOs;

import java.util.List;

public class SearchResultWrapperDTO {
    private List<FlashcardSearchResultDTO> flashcards;
    private List<QuizSearchResultDTO> quizzes;
    private List<UserSearchResultDTO> users;

    public List<FlashcardSearchResultDTO> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(List<FlashcardSearchResultDTO> flashcards) {
        this.flashcards = flashcards;
    }

    public List<QuizSearchResultDTO> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<QuizSearchResultDTO> quizzes) {
        this.quizzes = quizzes;
    }

    public List<UserSearchResultDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserSearchResultDTO> users) {
        this.users = users;
    }

    public SearchResultWrapperDTO() {
    }

    public SearchResultWrapperDTO(List<FlashcardSearchResultDTO> flashcards,
                                  List<QuizSearchResultDTO> quizzes,
                                  List<UserSearchResultDTO> users) {
        this.flashcards = flashcards;
        this.quizzes = quizzes;
        this.users = users;
    }
}
