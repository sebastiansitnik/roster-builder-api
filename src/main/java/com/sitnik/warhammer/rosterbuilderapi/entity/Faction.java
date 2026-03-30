package com.sitnik.warhammer.rosterbuilderapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "factions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Faction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotNull
    @Size(min=2, max = 30)
    private String name;

    @Column(nullable = false, length = 500)
    @NotNull
    @Size(min=2, max = 500)
    private String description;

}
