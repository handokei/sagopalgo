package org.example.domain.delivery.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.global.config.entity.BaseEntity;

@Entity
@Table(name = "deliveries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipientName;

    private String phoneNumber;

    private String address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private String trackingNumber;

    private Delivery(String recipientName, String phoneNumber, String address) {
        this.recipientName = recipientName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.status = DeliveryStatus.PENDING;
    }

    public static Delivery of(String recipientName, String phoneNumber, String address) {
        return new Delivery(recipientName, phoneNumber, address);
    }

    public void prepare() {
        this.status = DeliveryStatus.PREPARING;
    }

    public void ship(String trackingNumber) {
        this.status = DeliveryStatus.SHIPPED;
        this.trackingNumber = trackingNumber;
    }

    public void ship() {
        this.status = DeliveryStatus.SHIPPED;
    }

    public void complete() {
        this.status = DeliveryStatus.DELIVERED;
    }

    public void cancel() {
        this.status = DeliveryStatus.CANCELED;
    }
}
