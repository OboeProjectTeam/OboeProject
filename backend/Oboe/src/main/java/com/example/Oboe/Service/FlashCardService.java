package com.example.Oboe.Service;

import com.example.Oboe.DTOs.FlashCardDto;
import com.example.Oboe.Entity.FlashCards;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.FlashCardRepository;
import com.example.Oboe.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FlashCardService {

    @Autowired
    private FlashCardRepository flashCardRepository;

    @Autowired
    private UserRepository userRepository;

    public FlashCards createFlashCard(FlashCardDto dto, UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        FlashCards card = new FlashCards();
        card.setTerm(dto.getTerm());
        card.setDescription(dto.getDescription());
        card.setUser(user);

        return flashCardRepository.save(card);
    }

    public List<FlashCards> getFlashCardsByUser(UUID userId) {
        return flashCardRepository.findByUser_User_id(userId);
    }

    public boolean deleteFlashCard(UUID cardId, UUID userId) {
        FlashCards card = flashCardRepository.findById(cardId).orElse(null);
        if (card == null || !card.getUser().getUser_id().equals(userId)) {
            return false;
        }
        flashCardRepository.delete(card);
        return true;
    }
}
