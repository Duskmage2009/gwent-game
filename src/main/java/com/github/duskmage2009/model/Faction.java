package com.github.duskmage2009.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Faction {
    NORTHERN_REALMS("Northern Realms"),
    NILFGAARD("Nilfgaard"),
    SCOIATAEL("Scoia'tael"),
    SKELLIGE("Skellige"),
    MONSTERS("Monsters"),
    SYNDICATE("Syndicate"),
    NEUTRAL("Neutral");

    private final String displayName;

    Faction(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static Faction fromString(String value) {
        if (value == null) {
            return null;
        }

        for (Faction faction : Faction.values()) {
            if (faction.displayName.equalsIgnoreCase(value) ||
                    faction.name().equalsIgnoreCase(value)) {
                return faction;
            }
        }

        throw new IllegalArgumentException("Unknown faction: " + value);
    }

    @Override
    public String toString() {
        return displayName;
    }
}