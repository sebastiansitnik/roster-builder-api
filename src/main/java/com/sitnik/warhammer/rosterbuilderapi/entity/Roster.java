package com.sitnik.warhammer.rosterbuilderapi.entity;

import com.sitnik.warhammer.rosterbuilderapi.enums.PointsLimit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "rosters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Roster {

    @Id
    @GeneratedValue
    @UuidGenerator
    private String Id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private PointsLimit pointsLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faction_id")
    private Faction faction;

}
