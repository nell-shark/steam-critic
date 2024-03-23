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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "games", indexes = @Index(name = "name_index", columnList = "name"))
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "name"})
@JsonInclude(Include.NON_NULL)
public class Game {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private long id;

  @Column(name = "name", nullable = false)
  @lombok.NonNull
  private String name;

  @Column(name = "game_type", nullable = false)
  @lombok.NonNull
  private String gameType;

  @Column(name = "header_image", nullable = false)
  @lombok.NonNull
  private String headerImage;

  @Column(name = "platform", nullable = false)
  @CollectionTable(name = "platforms")
  @Enumerated(EnumType.STRING)
  @ElementCollection(targetClass = Platform.class, fetch = FetchType.LAZY)
  private List<Platform> platforms;

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

  @OneToMany(mappedBy = "game",
      fetch = FetchType.LAZY,
      orphanRemoval = true,
      cascade = CascadeType.REMOVE)
  private List<Price> prices;

  @PrePersist
  public void prePersist() {
    this.gameType = this.gameType.toUpperCase();
    this.prices = Optional.ofNullable(this.prices).orElse(List.of());
  }
}

