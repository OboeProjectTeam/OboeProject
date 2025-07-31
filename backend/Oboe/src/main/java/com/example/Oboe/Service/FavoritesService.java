package com.example.Oboe.Service;

import com.example.Oboe.DTOs.FavoritesDTO;
import com.example.Oboe.Entity.*;
import com.example.Oboe.Repository.*;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FavoritesService {

    private final KanjiRepository kanjiRepository;
    private final UserRepository userRepository;
    private final FavoritesRepository favoritesRepository;
    private final GrammarRepository grammarRepository;
    private final VocabularyRepository vocabularyRepository;
    private final SampleSentenceRepository sampleSentenceRepository;

    public FavoritesService(KanjiRepository kanjiRepository,
                            UserRepository userRepository,
                            FavoritesRepository favoritesRepository,
                            GrammarRepository grammarRepository,
                            VocabularyRepository vocabularyRepository, SampleSentenceRepository sampleSentenceRepository
                           ) {
        this.kanjiRepository = kanjiRepository;
        this.userRepository = userRepository;
        this.favoritesRepository = favoritesRepository;
        this.grammarRepository = grammarRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.sampleSentenceRepository = sampleSentenceRepository;
    }
    public FavoritesDTO toggleFavorite(FavoritesDTO dto, UUID userId) {
        Favorites existing = null;
        // Kiểm tra xem nội dung yêu thích là loại nào (Kanji, Grammar, Vocab, hoặc Sentence)
        // Và tìm trong database xem người dùng đã từng thích mục đó chưa.
        if (dto.getKanjiId() != null) {
            // Nếu là Kanji tìm trong bảng favorites theo userId + kanjiId
            existing = favoritesRepository.findFavoriteKanji(userId, dto.getKanjiId()).orElse(null);
        } else if (dto.getGrammaId() != null) {
            // Nếu là Grammar
            existing = favoritesRepository.findFavoriteGramma(userId, dto.getGrammaId()).orElse(null);
        } else if (dto.getVocabularyId() != null) {
            // Nếu là  (Vocabulary)
            existing = favoritesRepository.findFavoriteVocabulary(userId, dto.getVocabularyId()).orElse(null);
        } else if (dto.getSampleSentenceId() != null) {
            // Nếu là  (Sample Sentence)
            existing = favoritesRepository.findFavoriteSentence(userId, dto.getSampleSentenceId()).orElse(null);
        } else {
            // Không truyền loại nội dung nào → lỗi
            throw new RuntimeException("Phải cung cấp ít nhất 1 loại nội dung.");
        }
        // Nếu người dùng đã từng yêu thích nội dung này → xóa (bỏ yêu thích)
        if (existing != null) {
            favoritesRepository.delete(existing);
            return null; // Trả null nghĩa là đã bỏ yêu thích
        }
        // Nếu chưa từng yêu thích → tạo mới mục yêu thích
        return createFavorite(dto, userId);
    }
    public boolean isFavorited(UUID userId, String type, UUID targetId) {
        return switch (type) {
            case "kanji" -> favoritesRepository.findFavoriteKanji(userId, targetId).isPresent();
            case "grammar" -> favoritesRepository.findFavoriteGramma(userId, targetId).isPresent();
            case "vocabulary" -> favoritesRepository.findFavoriteVocabulary(userId, targetId).isPresent();
            case "samplesentence" -> favoritesRepository.findFavoriteSentence(userId, targetId).isPresent();
            default -> throw new IllegalArgumentException("Loại nội dung không hợp lệ: " + type);
        };
    }

    public FavoritesDTO createFavorite(FavoritesDTO dto, UUID userId) {
        Favorites favorites = new Favorites();

        favorites.setFavories_at(dto.getFavoritesAt() != null ? dto.getFavoritesAt() : java.time.LocalDate.now());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        favorites.setUser(user);

        if (dto.getKanjiId() != null) {
            Kanji kanji = kanjiRepository.findById(dto.getKanjiId())
                    .orElseThrow(() -> new RuntimeException("Kanji not found"));
            favorites.setKanji(kanji);
            favorites.setTitle(kanji.getCharacter_name());
            favorites.setContent(kanji.getMeaning());
        } else if (dto.getGrammaId() != null) {
            Grammar grammar = grammarRepository.findById(dto.getGrammaId())
                    .orElseThrow(() -> new RuntimeException("Grammar not found"));
            favorites.setGramma(grammar);
            favorites.setTitle(grammar.getStructure());
            favorites.setContent(grammar.getExplanation());
        }  else if (dto.getVocabularyId() != null) {
        Vocabulary vocabulary = vocabularyRepository.findById(dto.getVocabularyId())
                .orElseThrow(() -> new RuntimeException("Vocabulary not found"));
            favorites.setVocabulary(vocabulary);
            favorites.setTitle(vocabulary.getWords());
            favorites.setContent(vocabulary.getMeanning());
        }
        else if (dto.getSampleSentenceId() != null)  {
                SampleSentence sampleSentence = sampleSentenceRepository.findById(dto.getSampleSentenceId())
                        .orElseThrow(() -> new RuntimeException("SampleSentence not found"));
            favorites.setSentence(sampleSentence);
            favorites.setTitle(sampleSentence.getJapaneseText());
            favorites.setContent(sampleSentence.getVietnameseMeaning());
        }

        else {
            throw new RuntimeException("Phải cung cấp ít nhất 1 loại nội dung yêu thích (Kanji, Grammar, ...).");
        }

        favoritesRepository.save(favorites);
        return toDTO(favorites);
    }

    //lấy tất các từ yêu thích theo type
    public List<FavoritesDTO> getFavoritesByUserIdAndType(UUID userId, String type) {
        List<Favorites> list = favoritesRepository.findByUserId(userId);

        return list.stream()
                .filter(fav -> {
                    return switch (type.toLowerCase()) {
                        case "kanji" -> fav.getKanji() != null;
                        case "grammar" -> fav.getGramma() != null;
                        case "vocabulary" -> fav.getVocabulary() != null;
                        case "samplesentence" -> fav.getSentence() != null;
                        default -> false;
                    };
                })
                .map(fav -> {
                    FavoritesDTO dto = toDTO(fav);
                    dto.setType(type.toLowerCase());
                    return dto;
                })
                .toList();
    }
    //lấy tất cả các tử yêu thích
    public List<FavoritesDTO> getAllFavoritesByUserId(UUID userId) {
        List<Favorites> list = favoritesRepository.findByUserId(userId);

        return list.stream()
                .map(fav -> {
                    FavoritesDTO dto = toDTO(fav);

                    if (fav.getKanji() != null) {
                        dto.setType("kanji");
                    } else if (fav.getGramma() != null) {
                        dto.setType("grammar");
                    } else if (fav.getVocabulary() != null) {
                        dto.setType("vocabulary");
                    } else if (fav.getSentence() != null) {
                        dto.setType("samplesentence");
                    } else {
                        dto.setType("unknown");
                    }

                    return dto;
                })
                .toList();
    }
    // Xóa mục yêu thích dựa trên ID và kiểm tra quyền sở hữu của người dùng
    public void deleteFavorite(UUID favoriteId, UUID userId) {
        // Tìm mục yêu thích theo ID, nếu không tồn tại thì ném ra lỗi
        Favorites favorite = favoritesRepository.findById(favoriteId)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));

        // Kiểm tra xem mục yêu thích này có thuộc về người dùng hiện tại không
        if (!favorite.getUser().getUser_id().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa mục yêu thích này");
        }

        // Thực hiện xóa mục yêu thích khỏi cơ sở dữ liệu
        favoritesRepository.delete(favorite);
    }


    public static FavoritesDTO toDTO(Favorites favorites) {
        if (favorites == null) return null;
        FavoritesDTO dto = new FavoritesDTO(
                favorites.getFavoritesID(),
                favorites.getTitle(),
                favorites.getContent(),
                favorites.getFavories_at(),
                favorites.getUser() != null ? favorites.getUser().getUser_id() : null,
                favorites.getGramma() != null ? favorites.getGramma().getGrammaID() : null,
                favorites.getKanji() != null ? favorites.getKanji().getKanjiId() : null,
                favorites.getSentence() != null ? favorites.getSentence().getSample_sentence_id() : null,
                favorites.getVocabulary() != null ? favorites.getVocabulary().getVocalbId() : null
        );
        // Xác định type
        if (favorites.getKanji() != null) {
            dto.setType("kanji");
        } else if (favorites.getGramma() != null) {
            dto.setType("grammar");
        } else if (favorites.getVocabulary() != null) {
            dto.setType("vocabulary");
        } else if (favorites.getSentence() != null) {
            dto.setType("samplesentence");
        } else {
            dto.setType("unknown");
        }
        return dto;
    }


}

