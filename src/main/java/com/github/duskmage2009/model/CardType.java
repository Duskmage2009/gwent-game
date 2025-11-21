package com.github.duskmage2009.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CardType {
    UNIT("Unit"),
    SPECIAL("Special"),
    ARTIFACT("Artifact"),
    STRATAGEM("Stratagem");

    private final String displayName;

    CardType(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static CardType fromString(String value) {
        if (value == null) {
            return null;
        }

        for (CardType type : CardType.values()) {
            if (type.displayName.equalsIgnoreCase(value) ||
                    type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown card type: " + value);
    }

    @Override
    public String toString() {
        return displayName;
    }
}