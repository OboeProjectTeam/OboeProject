package com.example.Oboe.DTOs;

import java.util.UUID;

public interface TopicPostProjection  {
    //interface trong Spring Data JPA giúp ánh xạ kết quả truy vấn vào các cấu trúc đơn giản.
    String getBlogId();
    String getTitle();        // ánh xạ cột "tittle Blog" từ truy vấn
    Long getCommentCount();     // ánh xạ cột "totalPosts" từ truy vấn
}
