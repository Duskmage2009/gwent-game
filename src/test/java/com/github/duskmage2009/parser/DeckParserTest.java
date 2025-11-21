package com.github.duskmage2009.parser;


import com.github.duskmage2009.model.Card;
import com.github.duskmage2009.model.CardType;
import com.github.duskmage2009.model.Deck;
import com.github.duskmage2009.model.Faction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckParserTest {

    private DeckParser parser;

    @BeforeEach
    void setUp() {
        parser = new DeckParser();
    }

    @Test
    void testParseValidDeck(@TempDir Path tempDir) throws IOException {
        String json = """
                {
                  "name": "Test Deck",
                  "faction": "Northern Realms",
                  "leaderAbility": "Test Ability",
                  "provisionLimit": 150,
                  "categories": "Control, Tempo",
                  "cards": [
                    {
                      "name": "Test Card",
                      "provision": 5,
                      "power": 4,
                      "type": "Unit",
                      "faction": "Northern Realms"
                    }
                  ]
                }
                """;

        Path testFile = tempDir.resolve("test_deck.json");
        Files.writeString(testFile, json);

        Deck deck = parser.parse(testFile);

        assertNotNull(deck);
        assertEquals("Test Deck", deck.getName());
        assertEquals(Faction.NORTHERN_REALMS, deck.getFaction());
        assertEquals("Test Ability", deck.getLeaderAbility());
        assertEquals(150, deck.getProvisionLimit());
        assertEquals("Control, Tempo", deck.getCategories());
        assertEquals(1, deck.getCards().size());

        Card card = deck.getCards().get(0);
        assertEquals("Test Card", card.getName());
        assertEquals(5, card.getProvision());
        assertEquals(4, card.getPower());
        assertEquals(CardType.UNIT, card.getType());
        assertEquals(Faction.NORTHERN_REALMS, card.getFaction());
    }

    @Test
    void testParseMultipleCards(@TempDir Path tempDir) throws IOException {
        String json = """
                {
                  "name": "Multi Card Deck",
                  "faction": "Monsters",
                  "leaderAbility": "Overwhelming Hunger",
                  "provisionLimit": 160,
                  "categories": "Swarm, Devotion",
                  "cards": [
                    {
                      "name": "Card 1",
                      "provision": 4,
                      "power": 3,
                      "type": "Unit",
                      "faction": "Monsters"
                    },
                    {
                      "name": "Card 2",
                      "provision": 6,
                      "power": 0,
                      "type": "Special",
                      "faction": "Monsters"
                    },
                    {
                      "name": "Card 3",
                      "provision": 5,
                      "power": 0,
                      "type": "Artifact",
                      "faction": "Monsters"
                    }
                  ]
                }
                """;

        Path testFile = tempDir.resolve("multi_deck.json");
        Files.writeString(testFile, json);

        Deck deck = parser.parse(testFile);

        assertNotNull(deck);
        assertEquals(3, deck.getCards().size());
        assertEquals(CardType.UNIT, deck.getCards().get(0).getType());
        assertEquals(CardType.SPECIAL, deck.getCards().get(1).getType());
        assertEquals(CardType.ARTIFACT, deck.getCards().get(2).getType());
    }

    @Test
    void testParseDeckWithTotalPower(@TempDir Path tempDir) throws IOException {
        String json = """
                {
                  "name": "Power Test Deck",
                  "faction": "Skellige",
                  "leaderAbility": "Test",
                  "provisionLimit": 150,
                  "categories": "Midrange",
                  "cards": [
                    {
                      "name": "Strong Unit",
                      "provision": 10,
                      "power": 8,
                      "type": "Unit",
                      "faction": "Skellige"
                    },
                    {
                      "name": "Weak Unit",
                      "provision": 4,
                      "power": 3,
                      "type": "Unit",
                      "faction": "Skellige"
                    },
                    {
                      "name": "Special Card",
                      "provision": 6,
                      "power": 0,
                      "type": "Special",
                      "faction": "Skellige"
                    }
                  ]
                }
                """;

        Path testFile = tempDir.resolve("power_deck.json");
        Files.writeString(testFile, json);

        Deck deck = parser.parse(testFile);

        assertNotNull(deck);
        assertEquals(11, deck.getTotalUnitPower()); // 8 + 3 (Special card не считается)
        assertEquals(20, deck.getTotalProvisionUsed()); // 10 + 4 + 6
    }

    @Test
    void testParseCategoriesList(@TempDir Path tempDir) throws IOException {
        String json = """
                {
                  "name": "Category Test Deck",
                  "faction": "Nilfgaard",
                  "leaderAbility": "Test",
                  "provisionLimit": 150,
                  "categories": "Control, Removal, Mill, Lockdown",
                  "cards": []
                }
                """;

        Path testFile = tempDir.resolve("category_deck.json");
        Files.writeString(testFile, json);

        Deck deck = parser.parse(testFile);

        assertNotNull(deck);
        List<String> categories = deck.getCategoriesList();
        assertEquals(4, categories.size());
        assertTrue(categories.contains("Control"));
        assertTrue(categories.contains("Removal"));
        assertTrue(categories.contains("Mill"));
        assertTrue(categories.contains("Lockdown"));
    }

    @Test
    void testParseEmptyCategories(@TempDir Path tempDir) throws IOException {
        String json = """
                {
                  "name": "No Category Deck",
                  "faction": "Syndicate",
                  "leaderAbility": "Test",
                  "provisionLimit": 150,
                  "cards": []
                }
                """;

        Path testFile = tempDir.resolve("no_category_deck.json");
        Files.writeString(testFile, json);

        Deck deck = parser.parse(testFile);

        assertNotNull(deck);
        List<String> categories = deck.getCategoriesList();
        assertTrue(categories.isEmpty());
    }

    @Test
    void testParseInvalidJson(@TempDir Path tempDir) throws IOException {
        String invalidJson = "{ invalid json }";
        Path testFile = tempDir.resolve("invalid.json");
        Files.writeString(testFile, invalidJson);

        assertThrows(IOException.class, () -> parser.parse(testFile));
    }

    @Test
    void testParseNonExistentFile() {
        Path nonExistent = Path.of("/non/existent/file.json");
        assertThrows(IOException.class, () -> parser.parse(nonExistent));
    }

    @Test
    void testParseNeutralFaction(@TempDir Path tempDir) throws IOException {
        String json = """
                {
                  "name": "Neutral Deck",
                  "faction": "Northern Realms",
                  "leaderAbility": "Test",
                  "provisionLimit": 150,
                  "categories": "Neutral",
                  "cards": [
                    {
                      "name": "Neutral Card",
                      "provision": 9,
                      "power": 4,
                      "type": "Unit",
                      "faction": "Neutral"
                    }
                  ]
                }
                """;

        Path testFile = tempDir.resolve("neutral_deck.json");
        Files.writeString(testFile, json);

        Deck deck = parser.parse(testFile);

        assertNotNull(deck);
        assertEquals(Faction.NEUTRAL, deck.getCards().get(0).getFaction());
    }

    @Test
    void testParseStratagemCard(@TempDir Path tempDir) throws IOException {
        String json = """
                {
                  "name": "Stratagem Deck",
                  "faction": "Scoiatael",
                  "leaderAbility": "Test",
                  "provisionLimit": 150,
                  "categories": "Tempo",
                  "cards": [
                    {
                      "name": "Test Stratagem",
                      "provision": 13,
                      "power": 0,
                      "type": "Stratagem",
                      "faction": "Neutral"
                    }
                  ]
                }
                """;

        Path testFile = tempDir.resolve("stratagem_deck.json");
        Files.writeString(testFile, json);

        Deck deck = parser.parse(testFile);

        assertNotNull(deck);
        assertEquals(CardType.STRATAGEM, deck.getCards().get(0).getType());
    }
}