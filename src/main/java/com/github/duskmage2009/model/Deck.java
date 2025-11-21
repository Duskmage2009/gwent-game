package com.github.duskmage2009.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Deck {
    private String name;
    private Faction faction;

    @JsonProperty("leaderAbility")
    private String leaderAbility;

    @JsonProperty("provisionLimit")
    private Integer provisionLimit;

    private String categories;

    private List<Card> cards;

    public Deck() {
    }

    public Deck(String name, Faction faction, String leaderAbility, Integer provisionLimit, String categories, List<Card> cards) {
        this.name = name;
        this.faction = faction;
        this.leaderAbility = leaderAbility;
        this.provisionLimit = provisionLimit;
        this.categories = categories;
        this.cards = cards;
    }

    public int getTotalUnitPower() {
        if (cards == null) {
            return 0;
        }
        return cards.stream()
                .filter(card -> card.getType() == CardType.UNIT)
                .filter(card -> card.getPower() != null)
                .mapToInt(Card::getPower)
                .sum();
    }

    public int getTotalProvisionUsed() {
        if (cards == null) {
            return 0;
        }
        return cards.stream()
                .filter(card -> card.getProvision() != null)
                .mapToInt(Card::getProvision)
                .sum();
    }


    public List<String> getCategoriesList() {
        if (categories == null || categories.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(categories.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public String getLeaderAbility() {
        return leaderAbility;
    }

    public void setLeaderAbility(String leaderAbility) {
        this.leaderAbility = leaderAbility;
    }

    public Integer getProvisionLimit() {
        return provisionLimit;
    }

    public void setProvisionLimit(Integer provisionLimit) {
        this.provisionLimit = provisionLimit;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deck deck = (Deck) o;
        return Objects.equals(name, deck.name) &&
                faction == deck.faction &&
                Objects.equals(leaderAbility, deck.leaderAbility);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, faction, leaderAbility);
    }

    @Override
    public String toString() {
        return "Deck{" +
                "name='" + name + '\'' +
                ", faction=" + faction +
                ", leaderAbility='" + leaderAbility + '\'' +
                ", provisionLimit=" + provisionLimit +
                ", categories='" + categories + '\'' +
                ", cards=" + (cards != null ? cards.size() : 0) +
                ", totalUnitPower=" + getTotalUnitPower() +
                ", provisionUsed=" + getTotalProvisionUsed() +
                '}';
    }
}