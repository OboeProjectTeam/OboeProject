package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "CardItems")
public class CardItem {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column(name = "CardItemId", updatable = false, nullable = false)
        private UUID cardItemId;

        private String word;
        private String meaning;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "set_id", nullable = false)
        @JsonBackReference("set-carditems")
        private FlashCards flashCards;

        public CardItem() {}
        public CardItem(String word, String meaning) {
            this.word = word;
            this.meaning = meaning;
        }

        public UUID getCardItemId() {
            return cardItemId;
        }

        public void setCardItemId(UUID cardItemId) {
            this.cardItemId = cardItemId;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getMeaning() {
            return meaning;
        }

        public void setMeaning(String meaning) {
            this.meaning = meaning;
        }

        public FlashCards getFlashCards() {
            return flashCards;
        }

        public void setFlashCards(FlashCards flashCards) {
            this.flashCards = flashCards;
        }
}
