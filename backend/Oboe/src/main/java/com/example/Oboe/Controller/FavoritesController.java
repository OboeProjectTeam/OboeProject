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
@RequestMapping("/favorites")
public class FavoritesController {

    private final FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @PostMapping
    public ResponseEntity<FavoritesDTO> createFavorite( @Valid @RequestBody FavoritesDTO favoritesDTO, Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userid = userDetails.getUserID();
        FavoritesDTO created = favoritesService.createFavorite(favoritesDTO, userid);
        return ResponseEntity.ok(created);
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
