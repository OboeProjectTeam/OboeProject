package com.example.Oboe.Service;

import com.example.Oboe.DTOs.CommentDTOs;
import com.example.Oboe.DTOs.KanjiDTOs;
import com.example.Oboe.DTOs.ReadingDTO;
import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Entity.Kanji;
import com.example.Oboe.Entity.Reading;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.KanjiRepository;
import com.example.Oboe.Repository.ReadingRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class KanjiService {

    private final KanjiRepository kanjiRepository;
    private final ReadingRepository readingRepository;

    public KanjiService(KanjiRepository kanjiRepository, ReadingRepository readingRepository) {
        this.kanjiRepository = kanjiRepository;
        this.readingRepository = readingRepository;
    }
    //get All Kanji
    public Map<String, Object> getAllKanji(int page, int size) {
        // Tạo đối tượng phân trang
        Pageable pageable = PageRequest.of(page, size);

        // Lấy dữ liệu có phân trang
        Page<Kanji> kanjiPage = kanjiRepository.findAll(pageable);

        // Chuyển từ entity sang DTO
        List<KanjiDTOs> kanjiDTOs = kanjiPage.getContent()
                .stream()
                .map(this::kanjiToDTO)
                .collect(Collectors.toList());

        // Gói dữ liệu và thông tin phân trang vào map trả về
        Map<String, Object> response = new HashMap<>();
        response.put("kanjis", kanjiDTOs);
        response.put("currentPage", kanjiPage.getNumber());
        response.put("pageSize", kanjiPage.getSize());
        response.put("totalElements", kanjiPage.getTotalElements());
        response.put("totalPages", kanjiPage.getTotalPages());
        response.put("isLastPage", kanjiPage.isLast());

        return response;
    }

    //Crud
    public KanjiDTOs createKanji(KanjiDTOs dto){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        //  quyền ADMIN
        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new SecurityException("Bạn không có quyền tạo Kanji.");
        }

        Kanji kanji = new Kanji();
        kanji.setStrokes(dto.getStrokes());
        kanji.setMeaning(dto.getMeaning());
        kanji.setCharacter_name(dto.getCharacterName());

        Kanji saved = kanjiRepository.save(kanji);

        if (dto.getReadings() != null && !dto.getReadings().isEmpty()) {
            List<Reading> readings = new ArrayList<>();
            for (ReadingDTO readingDTO : dto.getReadings()) {
                readingDTO.setOwnerType("kanji");
                readingDTO.setOwnerId(saved.getKanjiId());
                readings.add(readingToEntity(readingDTO));
            }
            readingRepository.saveAll(readings);
        }

        return kanjiToDTO(saved);
    }
    public KanjiDTOs getKanjiByKanjiId(UUID kanjiId) {
        Kanji kanji = kanjiRepository.findById(kanjiId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Kanji với ID: " + kanjiId));
        return kanjiToDTO(kanji);
    }
    public KanjiDTOs updateKanji(KanjiDTOs dto, UUID kanjiId) {
        // quyền ROLE_ADMIN
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
                !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new SecurityException("Bạn không có quyền cập nhật Kanji.");
        }
        //  entity cần update
        Kanji kanji = getKanjiEntityById(kanjiId);
        if (kanji == null) return null;

        // Cập nhật các trường nếu khác null
        if (dto.getCharacterName() != null) {
            kanji.setCharacter_name(dto.getCharacterName());
        }
        if (dto.getMeaning() != null) {
            kanji.setMeaning(dto.getMeaning());
        }
        if (dto.getStrokes() != null) {
            kanji.setStrokes(dto.getStrokes());
        }
        // Lưu lại
        Kanji updated = kanjiRepository.save(kanji);

        // Xử lý cập nhật readings nếu có
        if (dto.getReadings() != null && !dto.getReadings().isEmpty()) {
            // Xoá readings cũ
            List<Reading> oldReadings = readingRepository.findByOwnerTypeAndOwnerId("kanji", kanjiId);
            readingRepository.deleteAll(oldReadings);

            // Thêm readings mới
            List<Reading> newReadings = new ArrayList<>();
            for (ReadingDTO readingDTO : dto.getReadings()) {
                readingDTO.setOwnerType("kanji");
                readingDTO.setOwnerId(kanjiId);
                newReadings.add(readingToEntity(readingDTO));
            }
            readingRepository.saveAll(newReadings);
        }

        // Trả về DTO mới
        return kanjiToDTO(updated);
    }
    public void deleteKanji(UUID kanjiId) {
        //  Check ROLE_ADMIN
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
                !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new SecurityException("Bạn không có quyền xóa Kanji.");
        }

        //  Tìm entity cần xóa
        Kanji kanji = getKanjiEntityById(kanjiId);
        if (kanji == null) throw new RuntimeException("Không tìm thấy Kanji với ID: " + kanjiId);

        //  Xoá readings
        List<Reading> readings = readingRepository.findByOwnerTypeAndOwnerId("kanji", kanjiId);
        readingRepository.deleteAll(readings);

        //  Xoá kanji
        kanjiRepository.delete(kanji);
    }
    public List<KanjiDTOs> searchKanji(String keyword) {
        List<Kanji> kanjis = kanjiRepository.searchKanji(keyword);
        return kanjis.stream()
                .map(this::kanjiToDTO)
                .collect(Collectors.toList());
    }
    public List<KanjiDTOs> getRelatedKanji(UUID kanjiId) {
        Kanji kanji = kanjiRepository.findById(kanjiId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Kanji"));

        String meaning = kanji.getMeaning();

        List<Kanji> relatedKanjis = kanjiRepository.findRelatedByMeaning(meaning, kanjiId);
        return relatedKanjis.stream().map(this::kanjiToDTO).collect(Collectors.toList());
    }


    public Kanji getKanjiEntityById(UUID commentId) {
        return kanjiRepository.findById(commentId).orElse(null);
    }





    private KanjiDTOs kanjiToDTO(Kanji kanji) {
        KanjiDTOs dto = new KanjiDTOs();
        dto.setKanjiId(kanji.getKanjiId());
        dto.setCharacterName(kanji.getCharacter_name());
        dto.setMeaning(kanji.getMeaning());
        dto.setStrokes(kanji.getStrokes());

        List<ReadingDTO> readings = readingRepository
                .findByOwnerTypeAndOwnerId("kanji", kanji.getKanjiId())
                .stream()
                .map(this::readingToDTO)
                .collect(Collectors.toList());

        dto.setReadings(readings);
        return dto;
    }
    private ReadingDTO readingToDTO(Reading reading) {
        return new ReadingDTO(
                reading.getReadingID(),
                reading.getReadingText(),
                reading.getReadingType(),
                reading.getOwnerType(),
                reading.getOwnerId()
        );
    }

    private Reading readingToEntity(ReadingDTO dto) {
        Reading reading = new Reading();
        reading.setReadingID(dto.getReadingID());
        reading.setReadingText(dto.getReadingText());
        reading.setReadingType(dto.getReadingType());
        reading.setOwnerType(dto.getOwnerType());
        reading.setOwnerId(dto.getOwnerId());
        return reading;
    }





}
