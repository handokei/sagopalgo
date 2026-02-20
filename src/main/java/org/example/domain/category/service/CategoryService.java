package org.example.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.category.controller.dto.CategoryCreateRequestDto;
import org.example.domain.category.controller.dto.CategoryResponseDto;
import org.example.domain.category.controller.dto.CategoryUpdateRequestDto;
import org.example.domain.category.domain.model.Category;
import org.example.domain.category.domain.repository.CategoryRepository;
import org.example.domain.category.exception.CategoryErrorCode;
import org.example.domain.category.exception.CategoryException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponseDto create(CategoryCreateRequestDto requestDto) {
        if (categoryRepository.existsByNameAndIsDeletedFalse(requestDto.getName())) {
            throw new CategoryException(CategoryErrorCode.DUPLICATE_CATEGORY_NAME);
        }

        Category category = Category.of(requestDto.getName(), requestDto.getDescription());
        categoryRepository.save(category);

        return CategoryResponseDto.from(category);
    }

    public List<CategoryResponseDto> getAll() {
        return categoryRepository.findAllByIsDeletedFalseOrderByNameAsc()
                .stream()
                .map(CategoryResponseDto::from)
                .toList();
    }

    public CategoryResponseDto getById(Long id) {
        Category category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        return CategoryResponseDto.from(category);
    }

    @Transactional
    public CategoryResponseDto update(Long id, CategoryUpdateRequestDto requestDto) {
        Category category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        if (requestDto.getName() != null &&
                !requestDto.getName().equals(category.getName()) &&
                categoryRepository.existsByNameAndIsDeletedFalse(requestDto.getName())) {
            throw new CategoryException(CategoryErrorCode.DUPLICATE_CATEGORY_NAME);
        }

        category.update(requestDto.getName(), requestDto.getDescription());

        return CategoryResponseDto.from(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        category.delete();
    }
}
