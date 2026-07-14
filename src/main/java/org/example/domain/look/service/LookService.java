package org.example.domain.look.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.look.controller.dto.LookCreateRequestDto;
import org.example.domain.look.controller.dto.LookItemRequestDto;
import org.example.domain.look.controller.dto.LookResponseDto;
import org.example.domain.look.domain.model.Look;
import org.example.domain.look.domain.model.LookItem;
import org.example.domain.look.domain.repository.LookRepository;
import org.example.domain.look.exception.LookErrorCode;
import org.example.domain.look.exception.LookException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LookService {

    private final LookRepository lookRepository;

    @Transactional
    public LookResponseDto create(LookCreateRequestDto requestDto) {
        Look look = Look.of(
                requestDto.getTitle(),
                requestDto.getSubtitle(),
                requestDto.getCurator(),
                requestDto.getHeroImageUrl()
        );

        AtomicInteger order = new AtomicInteger(0);
        for (LookItemRequestDto item : requestDto.getItems()) {
            look.addItem(LookItem.of(
                    item.getProductId(),
                    item.getLabel(),
                    item.getPrice(),
                    item.getImageUrl(),
                    order.getAndIncrement()
            ));
        }

        lookRepository.save(look);
        return LookResponseDto.from(look);
    }

    public LookResponseDto getToday() {
        Look look = lookRepository.findFirstByIsActiveTrueAndIsDeletedFalseOrderByCreatedAtDesc()
                .orElseThrow(() -> new LookException(LookErrorCode.LOOK_NOT_FOUND));

        return LookResponseDto.from(look);
    }

    public LookResponseDto getById(Long id) {
        Look look = lookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new LookException(LookErrorCode.LOOK_NOT_FOUND));

        return LookResponseDto.from(look);
    }

    @Transactional
    public void delete(Long id) {
        Look look = lookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new LookException(LookErrorCode.LOOK_NOT_FOUND));

        look.delete();
    }
}
