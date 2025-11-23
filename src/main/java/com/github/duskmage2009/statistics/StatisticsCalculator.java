package com.github.duskmage2009.statistics;

import com.github.duskmage2009.model.Card;
import com.github.duskmage2009.model.CardType;
import com.github.duskmage2009.model.Deck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class StatisticsCalculator {
    private static final Logger log = LoggerFactory.getLogger(StatisticsCalculator.class);

    public Map<String, Integer> calculateStatistics(List<Deck> decks, String attribute) {
        log.info("Calculating statistics for attribute: {}", attribute);

        Map<String, Integer> statistics = switch (attribute.toLowerCase()) {
            case "faction" -> calculateFactionStatistics(decks);
            case "type", "cardtype" -> calculateTypeStatistics(decks);
            case "provision" -> calculateProvisionStatistics(decks);
            case "power" -> calculatePowerStatistics(decks);
            case "leaderability" -> calculateLeaderAbilityStatistics(decks);
            case "totalpower" -> calculateTotalPowerStatistics(decks);
            case "deckfaction" -> calculateDeckFactionStatistics(decks);
            case "categories", "category" -> calculateCategoriesStatistics(decks);
            default -> throw new IllegalArgumentException(
                    "Unsupported attribute: " + attribute +
                            "\nSupported: faction, type, provision, power, leaderAbility, totalPower, deckFaction, categories"
            );
        };

        Map<String, Integer> sortedStatistics = statistics.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        log.info("Statistics calculated: {} unique values", sortedStatistics.size());
        return sortedStatistics;
    }

    private Map<String, Integer> calculateFactionStatistics(List<Deck> decks) {
        Map<String, Integer> stats = new HashMap<>();

        for (Deck deck : decks) {
            for (Card card : deck.getCards()) {
                String faction = card.getFaction() != null ?
                        card.getFaction().toString() : "UNKNOWN";
                stats.merge(faction, 1, Integer::sum);
            }
        }

        return stats;
    }

    private Map<String, Integer> calculateTypeStatistics(List<Deck> decks) {
        Map<String, Integer> stats = new HashMap<>();

        for (Deck deck : decks) {
            for (Card card : deck.getCards()) {
                String type = card.getType() != null ?
                        card.getType().toString() : "UNKNOWN";
                stats.merge(type, 1, Integer::sum);
            }
        }

        return stats;
    }

    private Map<String, Integer> calculateProvisionStatistics(List<Deck> decks) {
        Map<String, Integer> stats = new HashMap<>();

        for (Deck deck : decks) {
            for (Card card : deck.getCards()) {
                String provision = card.getProvision() != null ?
                        card.getProvision().toString() : "UNKNOWN";
                stats.merge(provision, 1, Integer::sum);
            }
        }

        return stats;
    }

    private Map<String, Integer> calculatePowerStatistics(List<Deck> decks) {
        Map<String, Integer> stats = new HashMap<>();

        for (Deck deck : decks) {
            for (Card card : deck.getCards()) {
                if (card.getType() == CardType.UNIT) {
                    String power = card.getPower() != null ?
                            card.getPower().toString() : "0";
                    stats.merge(power, 1, Integer::sum);
                }
            }
        }

        return stats;
    }

    private Map<String, Integer> calculateLeaderAbilityStatistics(List<Deck> decks) {
        Map<String, Integer> stats = new HashMap<>();

        for (Deck deck : decks) {
            String ability = deck.getLeaderAbility() != null ?
                    deck.getLeaderAbility() : "UNKNOWN";
            stats.merge(ability, 1, Integer::sum);
        }

        return stats;
    }

    private Map<String, Integer> calculateTotalPowerStatistics(List<Deck> decks) {
        Map<String, Integer> stats = new HashMap<>();

        for (Deck deck : decks) {
            int totalPower = deck.getTotalUnitPower();
            String powerRange = getPowerRange(totalPower);
            stats.merge(powerRange, 1, Integer::sum);
        }

        return stats;
    }

    private Map<String, Integer> calculateDeckFactionStatistics(List<Deck> decks) {
        Map<String, Integer> stats = new HashMap<>();

        for (Deck deck : decks) {
            String faction = deck.getFaction() != null ?
                    deck.getFaction().toString() : "UNKNOWN";
            stats.merge(faction, 1, Integer::sum);
        }

        return stats;
    }

    private Map<String, Integer> calculateCategoriesStatistics(List<Deck> decks) {
        Map<String, Integer> stats = new HashMap<>();

        for (Deck deck : decks) {
            List<String> categories = deck.getCategoriesList();
            for (String category : categories) {
                stats.merge(category, 1, Integer::sum);
            }
        }

        return stats;
    }

    private String getPowerRange(int totalPower) {
        if (totalPower == 0) return "0";
        if (totalPower <= 50) return "1-50";
        if (totalPower <= 100) return "51-100";
        if (totalPower <= 150) return "101-150";
        if (totalPower <= 200) return "151-200";
        return "200+";
    }

    public void printSummary(List<Deck> decks) {
        log.info("=== Deck Statistics Summary ===");
        System.out.println("\n=== Deck Statistics Summary ===");
        System.out.println("Total decks: " + decks.size());

        int totalCards = decks.stream()
                .mapToInt(d -> d.getCards().size())
                .sum();
        System.out.println("Total cards: " + totalCards);

        double avgCardsPerDeck = decks.isEmpty() ? 0 : (double) totalCards / decks.size();
        System.out.printf("Average cards per deck: %.2f\n", avgCardsPerDeck);

        int totalUnitPower = decks.stream()
                .mapToInt(Deck::getTotalUnitPower)
                .sum();
        System.out.println("Total unit power across all decks: " + totalUnitPower);

        double avgPowerPerDeck = decks.isEmpty() ? 0 : (double) totalUnitPower / decks.size();
        System.out.printf("Average unit power per deck: %.2f\n", avgPowerPerDeck);

        System.out.println("================================\n");
    }
}