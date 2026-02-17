package org.example.domain.delivery.domain.repository;

import org.example.domain.delivery.domain.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
