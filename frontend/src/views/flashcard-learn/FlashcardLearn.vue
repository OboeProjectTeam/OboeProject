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
  import { computed } from 'vue';
  import { useStore } from 'vuex'; // Import useStore to access Vuex state
  import TheCard from '@/components/card/TheCard.vue';
  
  const store = useStore(); // Access Vuex store
  
  // Lấy danh sách items từ store và chuyển đổi thành format slides
  const slides = computed(() => {
    const learningItems = store.getters['flashcard/getLearningItems'];
    console.log('Learning items:', learningItems); // Debug log
    
    return learningItems.map(item => {
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
          console.log('Grammar item:', item); // Debug log
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
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 40px;
        background: #f8f9fa;
    }
  </style>
  