package com.sitnik.warhammer.rosterbuilderapi.enums;

import lombok.Getter;

@Getter
public enum PointsLimit {
    COMBAT_PATROL(500),
    INCURSION(1000),
    STRIKE_FORCE(2000),
    ONSLAUGHT(3000),
    CUSTOM(-1);

    private final int value;

    PointsLimit(int value) {
        this.value = value;
    }

}