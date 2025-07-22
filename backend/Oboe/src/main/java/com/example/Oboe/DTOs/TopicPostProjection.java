package com.example.Oboe.DTOs;

public interface TopicPostProjection  {
    //interface trong Spring Data JPA giúp ánh xạ kết quả truy vấn vào các cấu trúc đơn giản.
    String getTopic();
    Long getTotalPosts();
}
