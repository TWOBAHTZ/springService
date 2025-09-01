package com.example.springservice.service;

import com.example.springservice.dto.CommissionCardCreateDTO;
import com.example.springservice.dto.CommissionCardResponseDTO;
import com.example.springservice.dto.CommissionCardUpdateDTO;
import com.example.springservice.entites.CommissionCard;
import com.example.springservice.entites.User;
import com.example.springservice.repo.CommissionCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CommissionCardService {

    @Autowired
    private CommissionCardRepository cardRepo;

    public CommissionCardResponseDTO createCard(User artist, CommissionCardCreateDTO dto) {
        CommissionCard card = new CommissionCard();
        card.setArtist(artist);
        card.setTitle(dto.title);
        card.setDescription(dto.description);
        card.setPrice(dto.price);
        card.setEstimatedDuration(dto.estimatedDuration);
        card.setSampleImageUrl(dto.sampleImageUrl);
        card.setOpen(true);

        return new CommissionCardResponseDTO(cardRepo.save(card));  // <-- น่าจะพังตรงนี้
    }


    public List<CommissionCardResponseDTO> getMyCards(User artist) {
        return cardRepo.findByArtist(artist).stream()
                .map(CommissionCardResponseDTO::new)
                .toList();
    }

    public List<CommissionCardResponseDTO> getPublicCards() {
        return cardRepo.findByOpenTrue().stream()
                .map(CommissionCardResponseDTO::new)
                .toList();
    }

    public ResponseEntity<?> updateCard(User artist, Integer id, CommissionCardUpdateDTO dto) {
        CommissionCard card = validateCardOrThrow(artist, id);

        card.setTitle(dto.title);
        card.setDescription(dto.description);
        card.setPrice(dto.price);
        card.setEstimatedDuration(dto.estimatedDuration);
        card.setSampleImageUrl(dto.sampleImageUrl);
        card.setOpen(dto.open);

        cardRepo.save(card);
        return ResponseEntity.ok(Map.of("message", "Commission card updated"));
    }

    public ResponseEntity<?> deleteCard(User artist, Integer id) {
        CommissionCard card = validateCardOrThrow(artist, id);
        cardRepo.delete(card);
        return ResponseEntity.ok(Map.of("message", "Commission card deleted"));
    }

    public ResponseEntity<?> toggleCommissionStatus(Integer cardId, User artist) {
        CommissionCard card = validateCardOrThrow(artist, cardId);
        card.setOpen(!card.isOpen());
        cardRepo.save(card);

        String status = card.isOpen() ? "opened" : "closed";
        return ResponseEntity.ok(Map.of("message", "Commission card " + status));
    }
    public CommissionCard validateCardOrThrow(User artist, Integer cardId) {
        CommissionCard card = cardRepo.findById(cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));
        if (!card.getArtist().getUserId().equals(artist.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your card");
        }
        return card;
    }






}


