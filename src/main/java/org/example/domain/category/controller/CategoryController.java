package org.example.domain.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.category.controller.dto.CategoryCreateRequestDto;
import org.example.domain.category.controller.dto.CategoryResponseDto;
import org.example.domain.category.controller.dto.CategoryUpdateRequestDto;
import org.example.domain.category.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDto> create(
            @RequestBody @Valid CategoryCreateRequestDto requestDto
    ) {
        CategoryResponseDto responseDto = categoryService.create(requestDto);
        return ResponseEntity.status(201).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAll() {
        List<CategoryResponseDto> responseDto = categoryService.getAll();
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getById(@PathVariable Long id) {
        CategoryResponseDto responseDto = categoryService.getById(id);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid CategoryUpdateRequestDto requestDto
    ) {
        CategoryResponseDto responseDto = categoryService.update(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
