<template>
    <div class="flashcard-learn">
      <TheCard 
        :slides="slides" 
        :width="700" 
        :height="400" 
        :pagination="{ clickable: true }" 
        :canFlip="true" 
      />
    </div>
  </template>
  
  <script setup>
  import { computed, onMounted } from 'vue';
  import { useStore } from 'vuex'; // Import useStore to access Vuex state
  import TheCard from '@/components/card/TheCard.vue';
  
  const store = useStore(); // Access Vuex store
  
  // Debug: Log khi component được mount
  onMounted(() => {
    const items = store.getters['flashcard/getLearningItems'];
    console.log('FlashcardLearn mounted, items:', items);
  });
  
  // Lấy danh sách items từ store và chuyển đổi thành format slides
  const slides = computed(() => {
    const learningItems = store.getters['flashcard/getLearningItems'];
    console.log('Learning items in slides computed:', learningItems);
    
    return learningItems.map(item => {
      // Xử lý đặc biệt cho flashcard type
      if (item.type === 'flashcard') {
        return {
          title: 'Thẻ ghi nhớ',
          content: item.kanji, // Mặt trước (front)
          description: '', // Có thể thêm mô tả nếu cần
          backcontent: item.meaning, // Mặt sau (back)
          backdescription: '', // Có thể thêm mô tả phụ nếu cần
          bgColor: '#ffffff',
          progressColor: '#E94560'
        };
      }

      // Giữ nguyên logic cũ cho các loại khác
      let mainText = '';
      let subText = '';
      let backText = '';
      let backSubText = '';

      switch (item.type) {
        case 'word':
          mainText = item.kanji || item.kana;
          subText = item.kana;
          backText = item.meaning;
          break;
        case 'kanji':
          mainText = item.kanji;
          subText = item.kunyomi;
          backText = item.kanjiname;
          backSubText = '';
          break;
        case 'grammar':
          mainText = item.kana || item.pattern;
          subText = item.pattern || '';
          backText = item.meaning;
          backSubText = item.note || item.explanation || '';
          break;
        case 'sentence':
          mainText = item.japanese || item.sentence;
          subText = '';
          backText = item.meaning || item.translation;
          break;
      }

      return {
        title: item.type === 'word' ? 'Từ vựng' :
               item.type === 'kanji' ? 'Hán tự' :
               item.type === 'grammar' ? 'Ngữ pháp' : 'Mẫu câu',
        content: mainText,
        description: subText,
        backcontent: backText,
        backdescription: backSubText,
        bgColor: '#ffffff',
        progressColor: '#E94560'
      };
    });
  });
  </script>
  
  <style scoped>
    .flashcard-learn{
        min-height: 100%;
        padding: 40px 20px;
    }
  </style>
  