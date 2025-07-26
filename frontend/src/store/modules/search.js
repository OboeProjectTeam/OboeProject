export default {
  namespaced: true,
  state: () => ({
    selectedWord: null,
    selectedKanji: null,
    selectedGrammar: null,
    selectedSentence: null
  }),
  mutations: {
    setSelectedWord(state, word) {
      state.selectedWord = word;
    },
    setSelectedKanji(state, kanji) {
      state.selectedKanji = kanji;
    },
    setSelectedGrammar(state, grammar) {
      state.selectedGrammar = grammar;
    },
    setSelectedSentence(state, sentence) {
      state.selectedSentence = sentence;
    },
    addComment(state, { type, itemId, comment }) {
      let item;
      switch (type) {
        case 'word':
          item = state.wordList.find(w => w.id === itemId);
          break;
        case 'kanji':
          item = state.KanjiList.find(k => k.id === itemId) || 
                state.KanjiList.find(k => k.kanji === itemId);
          break;
        case 'grammar':
          item = state.grammar.find(g => g.id === itemId);
          break;
        case 'sentence':
          item = state.sentenceList.find(s => s.id === itemId);
          break;
      }
      if (item) {
        if (!item.comments) {
          item.comments = [];
        }
        item.comments.unshift(comment);
      }
    }
  },
  actions: {
    async getWordById({ commit }, id) {
      try {
        // Import API dynamically to avoid circular dependency
        const api = (await import('@/api')).default;
        const word = await api.vocabulary.getById(id);
        
        // Map backend data structure to frontend expected structure
        const mappedWord = {
          id: word.vocalbId,
          kanji: word.words,
          kana: word.readings?.[0]?.reading || '',
          meaning: word.meanning,
          wordType: word.wordType,
          scriptType: word.scriptType,
          kanjiId: word.kanjiId,
          comments: [] // Comments will be loaded separately
        };
        
        commit('setSelectedWord', mappedWord);
        return mappedWord;
      } catch (error) {
        console.error('Error fetching word by ID:', error);

      
        return  null;
      }
    },
    
    async getKanjiById({ commit }, id) {
      try {
        const api = (await import('@/api')).default;
        const kanji = await api.kanji.getById(id);
        
        // Map backend data structure to frontend expected structure
        const mappedKanji = {
          id: kanji.kanjiId,
          kanji: kanji.characterName,
          kanjiname: kanji.meaning,
          reading: kanji.vietnamesePronunciation,
          meaning: kanji.meaning,
          strokes: kanji.strokes,
          comments: [] // Comments will be loaded separately
        };
        
        commit('setSelectedKanji', mappedKanji);
        return mappedKanji;
      } catch (error) {
        console.error('Error fetching kanji by ID:', error);
        
      
        return null;
      }
    },
    
    getKanjiByKanji({ state, commit }, kanjiChar) {
      const kanji = state.KanjiList.find(k => k.kanji === kanjiChar);
      commit('setSelectedKanji', kanji);
      return kanji;
    },
    
    async getGrammarById({ commit }, id) {
      try {
        const api = (await import('@/api')).default;
        const grammar = await api.grammar.getById(id);
        
        // Map backend data structure to frontend expected structure
        const mappedGrammar = {
          id: grammar.grammarId,
          kana: grammar.structure,
          romaji: grammar.vietnamesePronunciation,
          meaning: grammar.explanation,
          example: grammar.example,
          grammarType: grammar.grammarType,
          comments: [] // Comments will be loaded separately
        };
        
        commit('setSelectedGrammar', mappedGrammar);
        return mappedGrammar;
      } catch (error) {
        console.error('Error fetching grammar by ID:', error);
        return null;
      }
    },
    
    async getSentenceById({ commit }, id) {
      try {
        const api = (await import('@/api')).default;
        const sentence = await api.sampleSentence.getById(id);
        
        // Map backend data structure to frontend expected structure
        const mappedSentence = {
          id: sentence.id,
          sentence: sentence.japaneseText,
          translation: sentence.vietnameseMeaning,
          reading: '', // Not available in backend DTO
          comments: [] // Comments will be loaded separately
        };
        
        commit('setSelectedSentence', mappedSentence);
        return mappedSentence;
      } catch (error) {
        console.error('Error fetching sentence by ID:', error);
        return  null;
      }
    },
    
    addComment({ commit }, { type, itemId, comment }) {
      commit('addComment', { type, itemId, comment });
    }
  },
  getters: {
    wordList: (state) => state.wordList,
    selectedWord: (state) => state.selectedWord,
    selectedKanji: (state) => state.selectedKanji,
    selectedGrammar: (state) => state.selectedGrammar,
    selectedSentence: (state) => state.selectedSentence,
    getCommentsByTypeAndId: (state) => (type, itemId) => {
      let item;
      switch (type) {
        case 'word':
          item = state.wordList.find(w => w.id === itemId);
          break;
        case 'kanji':
          item = state.KanjiList.find(k => k.id === itemId) || 
                state.KanjiList.find(k => k.kanji === itemId);
          break;
        case 'grammar':
          item = state.grammar.find(g => g.id === itemId);
          break;
        case 'sentence':
          item = state.sentenceList.find(s => s.id === itemId);
          break;
      }
      return item?.comments || [];
    },
    getRelatedKanjiList: (state) => (word) => {
      if (!word || !word.kanji) return [];
      
      // Extract individual kanji characters from the word's kanji field
      const kanjiChars = Array.from(word.kanji).filter(char => {
        // Check if the character is a kanji using Unicode ranges
        const code = char.charCodeAt(0);
        return (code >= 0x4E00 && code <= 0x9FFF) || // CJK Unified Ideographs
               (code >= 0x3400 && code <= 0x4DBF) || // CJK Unified Ideographs Extension A
               (code >= 0x20000 && code <= 0x2A6DF); // CJK Unified Ideographs Extension B
      });

      // Find kanji details for each character
      return kanjiChars.map(kanjiChar => {
        const kanjiDetail = state.KanjiList.find(k => k.kanji === kanjiChar);
        return kanjiDetail || {
          kanji: kanjiChar,
          reading: '',
          kunyomi: '',
          meaning: 'Chưa có thông tin'
        };
      });
    }
  }
};
