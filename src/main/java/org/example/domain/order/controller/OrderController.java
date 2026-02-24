package org.example.domain.order.controller;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.example.domain.order.controller.dto.OrderCreateRequestDto;
import org.example.domain.order.controller.dto.OrderCreateResponseDto;
import org.example.domain.order.controller.dto.OrderResponseDto;
import org.example.domain.order.controller.dto.OrderStatusResponseDto;
import org.example.domain.order.service.OrderService;
import org.example.global.security.jwt.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/orders")
@AllArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    //주문 생성
    @PostMapping
    public ResponseEntity<OrderCreateResponseDto> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid OrderCreateRequestDto requestDto
            ) {
        Long userId = userDetails.getId();
        OrderCreateResponseDto responseDto = orderService.createOrder(userId, requestDto);
        return ResponseEntity.status(201).body(responseDto);
    }


    //다건 조회
    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size

    ) {
        Long userId = userDetails.getId();
        Page<OrderResponseDto> responseDto = orderService.getOrders(userId, page, size);

        return ResponseEntity.ok().body(responseDto);

    }

    //단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        Long userId = userDetails.getId();
        OrderResponseDto responseDto = orderService.getOrder(userId, id);
        return ResponseEntity.ok().body(responseDto);

    }

    //상태 수정
    @PatchMapping("/{id}/pay")
    public ResponseEntity<OrderStatusResponseDto> payOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        Long userId = userDetails.getId();
        OrderStatusResponseDto responseDto = orderService.payOrder(userId,id);
        return ResponseEntity.ok().body(responseDto);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderStatusResponseDto> cancelOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        Long userId = userDetails.getId();
        OrderStatusResponseDto responseDto = orderService.cancelOrder(userId, id);
        return ResponseEntity.ok().body(responseDto);
    }

    @PatchMapping("/{id}/ship")
    public ResponseEntity<OrderStatusResponseDto> shipOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        Long userId = userDetails.getId();
        OrderStatusResponseDto responseDto = orderService.shipOrder(userId, id);
        return ResponseEntity.ok().body(responseDto);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<OrderStatusResponseDto> completeOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    )     {
        Long userId = userDetails.getId();
        OrderStatusResponseDto responseDto = orderService.completeOrder(userId, id);
        return ResponseEntity.ok().body(responseDto);

    }




}
