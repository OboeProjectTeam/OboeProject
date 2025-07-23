package com.example.Oboe.DTOs;


//gom tất cả blog,comment, quizzes
public class ActivityDTO {
    private String type; // "blog", "comment", "Flashcard"
    private Object data; // Có thể là BlogDTO, CommentDTOs, Flashcard

    public ActivityDTO(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
