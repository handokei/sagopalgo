package org.example.domain.like.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.like.service.ProductLikeService;
import org.example.global.security.jwt.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/likes")
@RequiredArgsConstructor
@RestController
public class ProductLikeController {

    private final ProductLikeService  productLikeService;


    @PostMapping("/{id}")
    public ResponseEntity<Void> create(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {

        productLikeService.createLike(id, userDetails.getId());

        return ResponseEntity.ok().build();

    }


}
