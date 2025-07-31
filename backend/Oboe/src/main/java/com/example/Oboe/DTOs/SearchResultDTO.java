package com.example.Oboe.DTOs;

import java.util.List;

public class SearchResultDTO {
    private List<UserDTOs> users;

    public List<QuizSearchResultDTO> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<QuizSearchResultDTO> quizzes) {
        this.quizzes = quizzes;
    }

    public List<FlashCardDto> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(List<FlashCardDto> flashcards) {
        this.flashcards = flashcards;
    }

    public List<UserDTOs> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTOs> users) {
        this.users = users;
    }

    private List<QuizSearchResultDTO> quizzes;
    private List<FlashCardDto> flashcards;
    public SearchResultDTO() {
    }

    public SearchResultDTO(List<UserDTOs> users, List<QuizSearchResultDTO> quizzes, List<FlashCardDto> flashcards) {
        this.users = users;
        this.quizzes = quizzes;
        this.flashcards = flashcards;
    }

}

