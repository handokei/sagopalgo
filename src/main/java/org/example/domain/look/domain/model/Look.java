package org.example.domain.look.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.look.exception.LookErrorCode;
import org.example.domain.look.exception.LookException;
import org.example.global.config.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "looks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Look extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String subtitle;

    private String curator;

    @Column(nullable = false)
    private String heroImageUrl;

    private boolean isActive = true;

    private boolean isDeleted = false;

    @OneToMany(mappedBy = "look", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<LookItem> items = new ArrayList<>();

    private Look(String title, String subtitle, String curator, String heroImageUrl) {
        if (title == null || title.isBlank()) {
            throw new LookException(LookErrorCode.LOOK_TITLE_REQUIRED);
        }
        if (heroImageUrl == null || heroImageUrl.isBlank()) {
            throw new LookException(LookErrorCode.LOOK_HERO_IMAGE_REQUIRED);
        }
        this.title = title;
        this.subtitle = subtitle;
        this.curator = curator;
        this.heroImageUrl = heroImageUrl;
    }

    public static Look of(String title, String subtitle, String curator, String heroImageUrl) {
        return new Look(title, subtitle, curator, heroImageUrl);
    }

    public void addItem(LookItem item) {
        item.assignLook(this);
        this.items.add(item);
    }

    public void delete() {
        this.isDeleted = true;
    }
}
