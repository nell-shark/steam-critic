package com.nellshark.backend.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Entity
@Table(name = "games", indexes = @Index(name = "name_index", columnList = "name"))
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "name"})
@JsonInclude(Include.NON_NULL)
public class Game {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "game_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @Column(name = "header_image", nullable = false)
    private String headerImage;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "operating_system_requirements", joinColumns = @JoinColumn(name = "game_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "operating_system")
    @Column(name = "requirements", columnDefinition = "TEXT")
    private Map<OperatingSystem, String> operatingSystemRequirements;

    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    @Column(name = "release_date")
    @JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate releaseDate;

    @Column(name = "developers")
    private String developers;

    @Column(name = "publishers")
    private String publishers;

    @Column(name = "website")
    private String website;

    @Embedded
    private Metacritic metacritic;

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<Price> prices;

    public Game(long id,
                @NonNull String name,
                @NonNull GameType gameType,
                @NonNull String headerImage,
                @Nullable Map<OperatingSystem, String> operatingSystemRequirements,
                @Nullable String shortDescription,
                @Nullable LocalDate releaseDate,
                @Nullable String developers,
                @Nullable String publishers,
                @Nullable String website,
                @Nullable Metacritic metacritic) {
        this.id = id;
        this.name = name;
        this.gameType = gameType;
        this.headerImage = headerImage;
        this.releaseDate = releaseDate;
        this.operatingSystemRequirements = Optional.ofNullable(operatingSystemRequirements)
                .orElse(Collections.emptyMap());
        this.shortDescription = shortDescription;
        this.developers = developers;
        this.publishers = publishers;
        this.website = website;
        this.metacritic = metacritic;
    }
}

