package com.example.Oboe.Service;

import com.example.Oboe.DTOs.CardItemDto;
import com.example.Oboe.DTOs.FlashCardDto;
import com.example.Oboe.Entity.CardItem;
import com.example.Oboe.Entity.FlashCards;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.FlashCardRepository;
import com.example.Oboe.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlashCardService {

    private final FlashCardRepository flashCardRepository;
    private final UserRepository userRepository;

    // Tạo flashcard mới
    public FlashCards createFlashCard(FlashCardDto dto, UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        FlashCards flashCards = new FlashCards();
        flashCards.setTerm(dto.getTerm());
        flashCards.setDescription(dto.getDescription());
        flashCards.setUser(user);

        if (dto.getCardItems() != null) {
            for (CardItemDto itemDto : dto.getCardItems()) {
                CardItem cardItem = new CardItem();
                cardItem.setWord(itemDto.getWord());
                cardItem.setMeaning(itemDto.getMeaning());
                cardItem.setFlashCards(flashCards);
                flashCards.getCardItems().add(cardItem);
            }
        }

        return flashCardRepository.save(flashCards);
    }

    // Lấy flashcard theo user có phân trang
    public Page<FlashCards> getFlashCardsByUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));
        return flashCardRepository.findByUser(userId, pageable);
    }

    // Tìm flashcard theo term
    public Page<FlashCards> searchFlashCardsByTerm(UUID userId, String term, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));
        return flashCardRepository.searchByUserIdAndTerm(userId, term, pageable);
    }

    // Lấy flashcard theo ID
    public Optional<FlashCards> getFlashCardById(UUID cardId) {
        return flashCardRepository.findById(cardId);
    }

    // Xóa flashcard (kiểm tra quyền user)
    @Transactional
    public boolean deleteFlashCard(UUID cardId, UUID userId) {
        Optional<FlashCards> optionalCard = flashCardRepository.findById(cardId);
        if (optionalCard.isEmpty()) return false;

        FlashCards card = optionalCard.get();
        if (!card.getUser().getUser_id().equals(userId)) return false;

        flashCardRepository.delete(card);
        return true;
    }

    // Cập nhật flashcard
    @Transactional
    public FlashCards updateFlashCard(UUID cardId, FlashCardDto dto, UUID userId) {
        Optional<FlashCards> optionalCard = flashCardRepository.findById(cardId);
        if (optionalCard.isEmpty()) return null;

        FlashCards flashCards = optionalCard.get();
        if (!flashCards.getUser().getUser_id().equals(userId)) return null;

        flashCards.setTerm(dto.getTerm());
        flashCards.setDescription(dto.getDescription());
        flashCards.getCardItems().clear();

        if (dto.getCardItems() != null) {
            for (CardItemDto itemDto : dto.getCardItems()) {
                CardItem cardItem = new CardItem();
                cardItem.setWord(itemDto.getWord());
                cardItem.setMeaning(itemDto.getMeaning());
                cardItem.setFlashCards(flashCards);
                flashCards.getCardItems().add(cardItem);
            }
        }

        return flashCardRepository.save(flashCards);
    }

    // ✅ PHƯƠNG THỨC MỚI: Lấy flashcards ngẫu nhiên
    public List<FlashCards> getRandomFlashcards(UUID userId) {
        return flashCardRepository.findRandomFlashcardsForUser(userId,Pageable.unpaged());
    }

    // ✅ PHƯƠNG THỨC CŨ: Lấy ngữ cảnh từ flashcards ngẫu nhiên
    public String getRandomFlashcardsAsContext(UUID userId) {
        List<FlashCards> randomSets = flashCardRepository.findRandomFlashcardsForUser(userId,Pageable.unpaged());
        if (randomSets.isEmpty()) return null;

        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("Dựa vào các cặp từ vựng ngẫu nhiên sau đây từ flashcard của người dùng:\n");

        int itemCount = 0;
        for (FlashCards set : randomSets) {
            for (CardItem item : set.getCardItems()) {
                contextBuilder.append(String.format("- Từ: %s, Nghĩa: %s\n", item.getWord(), item.getMeaning()));
                itemCount++;
                if (itemCount >= 10) break;
            }
            if (itemCount >= 10) break;
        }

        return contextBuilder.toString();
    }

}
