package com.github.duskmage2009.statistics;

import com.github.duskmage2009.model.Card;
import com.github.duskmage2009.model.CardType;
import com.github.duskmage2009.model.Deck;
import com.github.duskmage2009.model.Faction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsCalculatorTest {

    private StatisticsCalculator calculator;
    private List<Deck> testDecks;

    @BeforeEach
    void setUp() {
        calculator = new StatisticsCalculator();
        testDecks = createTestDecks();
    }

    private List<Deck> createTestDecks() {
        List<Deck> decks = new ArrayList<>();

        List<Card> cards1 = List.of(
                new Card("Card 1", 4, 3, CardType.UNIT, Faction.NORTHERN_REALMS),
                new Card("Card 2", 4, 3, CardType.UNIT, Faction.NORTHERN_REALMS),
                new Card("Card 3", 5, 0, CardType.SPECIAL, Faction.NORTHERN_REALMS)
        );
        decks.add(new Deck("Deck 1", Faction.NORTHERN_REALMS, "Ability 1", 150, "Control, Tempo", cards1));

        List<Card> cards2 = List.of(
                new Card("Card 4", 6, 5, CardType.UNIT, Faction.MONSTERS),
                new Card("Card 5", 4, 3, CardType.UNIT, Faction.MONSTERS),
                new Card("Card 6", 7, 0, CardType.ARTIFACT, Faction.MONSTERS)
        );
        decks.add(new Deck("Deck 2", Faction.MONSTERS, "Ability 2", 160, "Swarm, Tempo, Devotion", cards2));

        return decks;
    }

    @Test
    void testCalculateFactionStatistics() {
        Map<String, Integer> stats = calculator.calculateStatistics(testDecks, "faction");

        assertNotNull(stats);
        assertEquals(2, stats.size());
        assertEquals(3, stats.get("Northern Realms"));
        assertEquals(3, stats.get("Monsters"));
    }

    @Test
    void testCalculateTypeStatistics() {
        Map<String, Integer> stats = calculator.calculateStatistics(testDecks, "type");

        assertNotNull(stats);
        assertEquals(3, stats.size());
        assertEquals(4, stats.get("Unit"));
        assertEquals(1, stats.get("Special"));
        assertEquals(1, stats.get("Artifact"));
    }

    @Test
    void testCalculateProvisionStatistics() {
        Map<String, Integer> stats = calculator.calculateStatistics(testDecks, "provision");

        assertNotNull(stats);
        assertTrue(stats.containsKey("4"));
        assertTrue(stats.containsKey("5"));
        assertTrue(stats.containsKey("6"));
        assertTrue(stats.containsKey("7"));
        assertEquals(3, stats.get("4"));
        assertEquals(1, stats.get("5"));
        assertEquals(1, stats.get("6"));
        assertEquals(1, stats.get("7"));
    }

    @Test
    void testCalculatePowerStatistics() {
        Map<String, Integer> stats = calculator.calculateStatistics(testDecks, "power");

        assertNotNull(stats);
        assertEquals(3, stats.get("3"));
        assertEquals(1, stats.get("5"));
    }

    @Test
    void testCalculateLeaderAbilityStatistics() {
        Map<String, Integer> stats = calculator.calculateStatistics(testDecks, "leaderAbility");

        assertNotNull(stats);
        assertEquals(2, stats.size());
        assertEquals(1, stats.get("Ability 1"));
        assertEquals(1, stats.get("Ability 2"));
    }

    @Test
    void testCalculateTotalPowerStatistics() {
        Map<String, Integer> stats = calculator.calculateStatistics(testDecks, "totalPower");

        assertNotNull(stats);
        assertEquals(2, stats.get("1-50"));
    }

    @Test
    void testCalculateDeckFactionStatistics() {
        Map<String, Integer> stats = calculator.calculateStatistics(testDecks, "deckFaction");

        assertNotNull(stats);
        assertEquals(2, stats.size());
        assertEquals(1, stats.get("Northern Realms"));
        assertEquals(1, stats.get("Monsters"));
    }

    @Test
    void testCalculateCategoriesStatistics() {
        Map<String, Integer> stats = calculator.calculateStatistics(testDecks, "categories");

        assertNotNull(stats);
        assertEquals(4, stats.size());
        assertEquals(2, stats.get("Tempo"));
        assertEquals(1, stats.get("Control"));
        assertEquals(1, stats.get("Swarm"));
        assertEquals(1, stats.get("Devotion"));
    }

    @Test
    void testStatisticsSortedByCount() {
        List<Card> manyCards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            manyCards.add(new Card("NR Card", 4, 3, CardType.UNIT, Faction.NORTHERN_REALMS));
        }
        for (int i = 0; i < 5; i++) {
            manyCards.add(new Card("Monster Card", 5, 4, CardType.UNIT, Faction.MONSTERS));
        }
        for (int i = 0; i < 2; i++) {
            manyCards.add(new Card("Neutral Card", 6, 5, CardType.UNIT, Faction.NEUTRAL));
        }

        List<Deck> decks = List.of(
                new Deck("Test", Faction.NORTHERN_REALMS, "Test", 150, "Control", manyCards)
        );

        Map<String, Integer> stats = calculator.calculateStatistics(decks, "faction");

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(stats.entrySet());

        assertEquals("Northern Realms", entries.get(0).getKey());
        assertEquals(10, entries.get(0).getValue());

        assertEquals("Monsters", entries.get(1).getKey());
        assertEquals(5, entries.get(1).getValue());

        assertEquals("Neutral", entries.get(2).getKey());
        assertEquals(2, entries.get(2).getValue());
    }

    @Test
    void testUnsupportedAttribute() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateStatistics(testDecks, "unsupported"));

        assertTrue(exception.getMessage().contains("Unsupported attribute"));
    }

    @Test
    void testEmptyDeckList() {
        Map<String, Integer> stats = calculator.calculateStatistics(new ArrayList<>(), "faction");
        assertNotNull(stats);
        assertTrue(stats.isEmpty());
    }

    @Test
    void testDeckTotalPowerCalculation() {
        Deck deck = testDecks.get(0);
        assertEquals(6, deck.getTotalUnitPower());
    }

    @Test
    void testDeckProvisionCalculation() {
        Deck deck = testDecks.get(0);
        assertEquals(13, deck.getTotalProvisionUsed());
    }

    @Test
    void testPowerStatisticsIgnoresNonUnits() {
        List<Card> mixedCards = List.of(
                new Card("Unit", 5, 5, CardType.UNIT, Faction.MONSTERS),
                new Card("Special", 6, 0, CardType.SPECIAL, Faction.MONSTERS),
                new Card("Artifact", 7, 0, CardType.ARTIFACT, Faction.MONSTERS),
                new Card("Stratagem", 13, 0, CardType.STRATAGEM, Faction.MONSTERS)
        );

        List<Deck> decks = List.of(
                new Deck("Mixed", Faction.MONSTERS, "Test", 150, "Mixed", mixedCards)
        );

        Map<String, Integer> stats = calculator.calculateStatistics(decks, "power");

        assertEquals(1, stats.size());
        assertEquals(1, stats.get("5"));
    }

    @Test
    void testCategoriesWithEmptyString() {
        List<Deck> decks = List.of(
                new Deck("Empty Cat", Faction.SKELLIGE, "Test", 150, "", new ArrayList<>())
        );

        Map<String, Integer> stats = calculator.calculateStatistics(decks, "categories");
        assertTrue(stats.isEmpty());
    }

    @Test
    void testCategoriesWithWhitespace() {
        List<Deck> decks = List.of(
                new Deck("Whitespace", Faction.SCOIATAEL, "Test", 150, "  Control  ,  Tempo  ", new ArrayList<>())
        );

        Map<String, Integer> stats = calculator.calculateStatistics(decks, "categories");
        assertEquals(2, stats.size());
        assertEquals(1, stats.get("Control"));
        assertEquals(1, stats.get("Tempo"));
    }
}