package org.example.domain.viewhistory.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.viewhistory.domain.model.ViewHistory;
import org.example.domain.viewhistory.domain.repository.ViewHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ViewHistoryService {

    private final ViewHistoryRepository viewHistoryRepository;

    @Transactional
    public void record(Long userId, Long productId, Long categoryId) {
        if (!viewHistoryRepository.existsByUserIdAndProductId(userId, productId)) {
            ViewHistory viewHistory = ViewHistory.of(userId, productId, categoryId);
            viewHistoryRepository.save(viewHistory);
        }
    }

    public List<Long> findUsersByProductId(Long productId) {
        return viewHistoryRepository.findUserIdsByProductId(productId);
    }

    public List<Long> findUsersByCategoryId(Long categoryId) {
        return viewHistoryRepository.findUserIdsByCategoryId(categoryId);
    }
}
