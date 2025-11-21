package com.github.duskmage2009.model;

import java.util.Objects;

public class Card {
    private String name;
    private Integer provision;
    private Integer power;
    private CardType type;
    private Faction faction;

    public Card() {
    }

    public Card(String name, Integer provision, Integer power, CardType type, Faction faction) {
        this.name = name;
        this.provision = provision;
        this.power = power;
        this.type = type;
        this.faction = faction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProvision() {
        return provision;
    }

    public void setProvision(Integer provision) {
        this.provision = provision;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(name, card.name) &&
                Objects.equals(provision, card.provision) &&
                Objects.equals(power, card.power) &&
                type == card.type &&
                faction == card.faction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, provision, power, type, faction);
    }

    @Override
    public String toString() {
        return "Card{" +
                "name='" + name + '\'' +
                ", provision=" + provision +
                ", power=" + power +
                ", type=" + type +
                ", faction=" + faction +
                '}';
    }
}