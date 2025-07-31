package com.example.Oboe.Controller;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.BlogDTO;
import com.example.Oboe.DTOs.FavoritesDTO;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Service.FavoritesService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/favorites")
public class FavoritesController {

    private final FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    // API để toggle (thêm hoặc hủy) yêu thích một nội dung (Kanji, Grammar, Vocabulary, SampleSentence)
    @PostMapping("/toggle")
    public ResponseEntity<?> toggleFavorite(@RequestBody FavoritesDTO dto, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();
        FavoritesDTO result = favoritesService.toggleFavorite(dto, userId);
        return ResponseEntity.ok(result == null ? "Đã hủy yêu thích" : result);
    }

    // API để kiểm tra một nội dung cụ thể đã được người dùng yêu thích hay chưa
    // Truyền vào type (kanji | grammar | vocabulary | samplesentence) và targetId (UUID của nội dung)
    // Trả về true nếu đã yêu thích, false nếu chưa
    @GetMapping("/is-favorited")
    public ResponseEntity<Boolean> isFavorited(
            Authentication authentication,
            @RequestParam String type,
            @RequestParam UUID targetId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();
        boolean isFavorited = favoritesService.isFavorited(userId, type.toLowerCase(), targetId);
        return ResponseEntity.ok(isFavorited);
    }

    @GetMapping("/user")
    public ResponseEntity<List<FavoritesDTO>> getUserFavorites(
            Authentication authentication,
            @RequestParam(required = false) String type
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();

        List<FavoritesDTO> favoritesList;

        if (type != null && !type.isEmpty()) {
            favoritesList = favoritesService.getFavoritesByUserIdAndType(userId, type);
        } else {
            favoritesList = favoritesService.getAllFavoritesByUserId(userId);
        }
        return ResponseEntity.ok(favoritesList);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFavorite(
            @PathVariable("id") UUID favoriteId,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();

        favoritesService.deleteFavorite(favoriteId, userId);
        return ResponseEntity.ok("Xóa mục yêu thích thành công!");
    }

}
