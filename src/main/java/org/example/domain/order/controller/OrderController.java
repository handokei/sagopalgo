package org.example.domain.order.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.domain.order.controller.dto.OrderCreateRequestDto;
import org.example.domain.order.controller.dto.OrderCreateResponseDto;
import org.example.domain.order.controller.dto.OrderResponseDto;
import org.example.domain.order.controller.dto.OrderStatusResponseDto;
import org.example.domain.order.controller.dto.ShipOrderRequestDto;
import org.example.domain.order.service.OrderFacade;
import org.example.domain.order.service.OrderService;
import org.example.global.security.jwt.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/orders")
@AllArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;
    private final OrderFacade orderFacade;

    @PostMapping
    public ResponseEntity<OrderCreateResponseDto> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid OrderCreateRequestDto requestDto
    ) {
        Long userId = userDetails.getId();
        OrderCreateResponseDto responseDto = orderFacade.createOrder(userId, requestDto);
        return ResponseEntity.status(201).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = userDetails.getId();
        return ResponseEntity.ok().body(orderService.getOrders(userId, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        Long userId = userDetails.getId();
        return ResponseEntity.ok().body(orderService.getOrder(userId, id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderStatusResponseDto> cancelOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        Long userId = userDetails.getId();
        return ResponseEntity.ok().body(orderFacade.cancelOrder(userId, id));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponseDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok().body(orderService.getAllOrders(page, size));
    }

    @PatchMapping("/{id}/ship")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderStatusResponseDto> shipOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestBody(required = false) ShipOrderRequestDto requestDto
    ) {
        Long userId = userDetails.getId();
        String trackingNumber = requestDto != null ? requestDto.getTrackingNumber() : null;
        return ResponseEntity.ok().body(orderFacade.shipOrder(userId, id, trackingNumber));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderStatusResponseDto> completeOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        Long userId = userDetails.getId();
        return ResponseEntity.ok().body(orderFacade.completeOrder(userId, id));
    }
}
