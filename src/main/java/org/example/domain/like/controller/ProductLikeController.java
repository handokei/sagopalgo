package org.example.domain.like.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.like.service.ProductLikeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/likes")
@RequiredArgsConstructor
@RestController
public class ProductLikeController {

    private final ProductLikeService  likeService;
}
