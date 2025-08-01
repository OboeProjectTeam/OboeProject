package com.example.Oboe.Service;

import com.example.Oboe.DTOs.BlogDTO;
import com.example.Oboe.DTOs.CardItemDto;
import com.example.Oboe.DTOs.FlashCardDto;
import com.example.Oboe.DTOs.FlashcardSearchResultDTO;
import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.CardItem;
import com.example.Oboe.Entity.FlashCards;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.FlashCardRepository;
import com.example.Oboe.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FlashCardService {

    @Autowired
    private FlashCardRepository flashCardRepository;
    private UserRepository userRepository;
    private UserService userService ;

    @Autowired
    public FlashCardService(FlashCardRepository flashCardRepository,
                            UserRepository userRepository,
                            UserService userService) {
        this.flashCardRepository = flashCardRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }


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

    public Page<FlashCards> getFlashCardsByUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));
        return flashCardRepository.findByUser(userId, pageable);
    }
    public List<FlashCardDto> getFlashcardById(UUID flashcardId) {
        return flashCardRepository.findById(flashcardId)
                .map(card -> List.of(convertToDto(card)))
                .orElse(List.of());
    }


    public Page<FlashCards> searchFlashCardsByTerm(UUID userId, String term, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));
        return flashCardRepository.searchByUserIdAndTerm(userId, term, pageable);
    }

     public List<FlashCards> getTop5LatestFlashCards(UUID userId) {
         return flashCardRepository.findTop5ByUserIdOrderByCreatedDesc(userId)
                 .stream()
                 .limit(5)
                 .toList();
     }

    public Optional<FlashCards> getFlashCardById(UUID cardId) {
        return flashCardRepository.findById(cardId);
    }

    @Transactional
    public boolean deleteFlashCard(UUID cardId, UUID userId) {
        Optional<FlashCards> optionalCard = flashCardRepository.findById(cardId);
        if (optionalCard.isEmpty()) return false;

        FlashCards card = optionalCard.get();
        if (!card.getUser().getUser_id().equals(userId)) return false;

        flashCardRepository.delete(card);
        return true;
    }

    public List<FlashCardDto> getAllflashByUserId(UUID userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return List.of();

        List<FlashCards> flashcards = flashCardRepository.findflashcardByUserId(userId);
        return flashcards.stream()
                .map(this::convertToDto)
                .toList();
    }


    public List<FlashCards> getAllFlashCards() {
        return flashCardRepository.findAll();
    }


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

    private FlashCardDto convertToDto(FlashCards flashCards) {
        FlashCardDto dto = new FlashCardDto();
        dto.setTerm(flashCards.getTerm());
        dto.setDescription(flashCards.getDescription());
        dto.setFlashcardID(flashCards.getSet_id());
        List<CardItemDto> itemDtos = flashCards.getCardItems().stream()
                .map(item -> {
                    CardItemDto itemDto = new CardItemDto();
                    itemDto.setWord(item.getWord());
                    itemDto.setMeaning(item.getMeaning());
                    return itemDto;
                })
                .toList();
        dto.setCardItems(itemDtos);
        return dto;
    }




}
