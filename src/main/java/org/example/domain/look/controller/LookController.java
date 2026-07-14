package org.example.domain.look.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.look.controller.dto.LookCreateRequestDto;
import org.example.domain.look.controller.dto.LookResponseDto;
import org.example.domain.look.service.LookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/looks")
@RequiredArgsConstructor
public class LookController {

    private final LookService lookService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LookResponseDto> create(
            @RequestBody @Valid LookCreateRequestDto requestDto
    ) {
        LookResponseDto responseDto = lookService.create(requestDto);
        return ResponseEntity.status(201).body(responseDto);
    }

    @GetMapping("/today")
    public ResponseEntity<LookResponseDto> getToday() {
        return ResponseEntity.ok(lookService.getToday());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LookResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(lookService.getById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
