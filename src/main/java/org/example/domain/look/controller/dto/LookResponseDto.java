package org.example.domain.look.controller.dto;

import lombok.Getter;
import org.example.domain.look.domain.model.Look;
import org.example.domain.look.domain.model.LookItem;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class LookResponseDto {

    private final Long id;
    private final String title;
    private final String subtitle;
    private final String curator;
    private final String heroImage;
    private final LocalDateTime createdAt;
    private final List<Hotspot> hotspots;

    private LookResponseDto(Look look) {
        this.id = look.getId();
        this.title = look.getTitle();
        this.subtitle = look.getSubtitle();
        this.curator = look.getCurator();
        this.heroImage = look.getHeroImageUrl();
        this.createdAt = look.getCreatedAt();
        this.hotspots = look.getItems().stream().map(Hotspot::new).toList();
    }

    public static LookResponseDto from(Look look) {
        return new LookResponseDto(look);
    }

    @Getter
    public static class Hotspot {
        private final Long id;
        private final Long productId;
        private final String label;
        private final String price;
        private final String image;

        private Hotspot(LookItem item) {
            this.id = item.getId();
            this.productId = item.getProductId();
            this.label = item.getLabel();
            this.price = item.getPrice();
            this.image = item.getImageUrl();
        }
    }
}
