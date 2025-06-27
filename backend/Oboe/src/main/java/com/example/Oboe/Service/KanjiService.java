package com.example.Oboe.Service;

import com.example.Oboe.DTOs.KanjiDTO;
import com.example.Oboe.Entity.Kanji;
import com.example.Oboe.Entity.Level;
import com.example.Oboe.Repository.KanjiRepository;
import com.example.Oboe.Repository.LevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class KanjiService {

    private KanjiRepository kanjiRepository;


    private LevelRepository levelRepository;
    @Autowired
    public KanjiService(KanjiRepository kanjiRepository, LevelRepository levelRepository) {
        this.kanjiRepository = kanjiRepository;
        this.levelRepository = levelRepository;
    }

    public List<KanjiDTO> getAllKanjis(){
        return kanjiRepository.findAll().stream().map(this::converToDTO).collect(Collectors.toList());
    }
    public KanjiDTO getKanjiById(UUID id){
        return converToDTO(kanjiRepository.findById(id).orElse(null));
    }

    public KanjiDTO createKanji(KanjiDTO kanjiDTO){
        if (kanjiRepository.existsByCharacterName(kanjiDTO.getCharacter_name().trim())) {
            throw new IllegalArgumentException("Kanji đã tồn tại");
        }
        Kanji kanji = convertDTOToKanji(kanjiDTO);
        return converToDTO(kanjiRepository.save(kanji));
    }
    public boolean deleteKanji(UUID id) {
        if (!kanjiRepository.existsById(id)) return false;
        kanjiRepository.deleteById(id);
        return true;
    }
    public List<KanjiDTO> searchKanji(String keyword) {
        List<Kanji> kanjiList = kanjiRepository.findByCharacterNameContainingIgnoreCase(keyword);
        return kanjiList.stream().map(this::converToDTO).toList();
    }
   private KanjiDTO  converToDTO(Kanji kanji) {
        if (kanji == null) return null;
       KanjiDTO kanjiDTO = new KanjiDTO();
       kanjiDTO.setKanjiID(kanji.getKanjiID());
       kanjiDTO.setCharacter_name(kanji.getCharacter_name());
       kanjiDTO.setOnyomi(kanji.getOnyomi());
       kanjiDTO.setKunyomi(kanji.getKunyomi());
       kanjiDTO.setMeaning(kanji.getMeaning());
       kanjiDTO.setStrokes(kanji.getStrokes());
       kanjiDTO.setExample(kanji.getExample());
       if (kanji.getLevel() != null) kanjiDTO.setLevelID(kanji.getLevel().getLevelID());
       return kanjiDTO ;
   };
   public Kanji convertDTOToKanji(KanjiDTO dto) {
       Kanji kanji = new Kanji();
       kanji.setKanjiID(dto.getKanjiID());
       kanji.setCharacter_name(dto.getCharacter_name());
       kanji.setOnyomi(dto.getOnyomi());
       kanji.setKunyomi(dto.getKunyomi());
       kanji.setMeaning(dto.getMeaning());
       kanji.setStrokes(dto.getStrokes());
       kanji.setExample(dto.getExample());
       if (dto.getLevelID() != null) {
           Level level = levelRepository.findById(dto.getLevelID()).orElse(null);
           kanji.setLevel(level);
       }
       return kanji;
   }


}
