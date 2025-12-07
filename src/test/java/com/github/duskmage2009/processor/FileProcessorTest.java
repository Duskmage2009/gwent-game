package com.github.duskmage2009.processor;

import com.github.duskmage2009.model.Deck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileProcessorTest {

    private FileProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new FileProcessor(2);
    }

    @Test
    void testProcessEmptyDirectory(@TempDir Path tempDir) throws IOException, InterruptedException {
        List<Deck> decks = processor.processDirectory(tempDir);

        assertNotNull(decks);
        assertTrue(decks.isEmpty());
    }

    @Test
    void testProcessDirectoryWithSingleFile(@TempDir Path tempDir) throws IOException, InterruptedException {
        String json = """
                {
                  "name": "Test Deck",
                  "faction": "Northern Realms",
                  "leaderAbility": "Test",
                  "provisionLimit": 150,
                  "categories": "Control",
                  "cards": [
                    {
                      "name": "Card",
                      "provision": 5,
                      "power": 4,
                      "type": "Unit",
                      "faction": "Northern Realms"
                    }
                  ]
                }
                """;

        Path testFile = tempDir.resolve("deck1.json");
        Files.writeString(testFile, json);

        List<Deck> decks = processor.processDirectory(tempDir);

        assertNotNull(decks);
        assertEquals(1, decks.size());
        assertEquals("Test Deck", decks.get(0).getName());
    }

    @Test
    void testProcessDirectoryWithMultipleFiles(@TempDir Path tempDir) throws IOException, InterruptedException {
        for (int i = 1; i <= 3; i++) {
            String json = String.format("""
                    {
                      "name": "Deck %d",
                      "faction": "Monsters",
                      "leaderAbility": "Ability %d",
                      "provisionLimit": 150,
                      "categories": "Swarm",
                      "cards": []
                    }
                    """, i, i);

            Path testFile = tempDir.resolve("deck" + i + ".json");
            Files.writeString(testFile, json);
        }

        List<Deck> decks = processor.processDirectory(tempDir);

        assertNotNull(decks);
        assertEquals(3, decks.size());
    }

    @Test
    void testProcessDirectoryWithArrayFormat(@TempDir Path tempDir) throws IOException, InterruptedException {
        String json = """
                [
                  {
                    "name": "Deck 1",
                    "faction": "Northern Realms",
                    "leaderAbility": "Ability 1",
                    "provisionLimit": 150,
                    "categories": "Control",
                    "cards": []
                  },
                  {
                    "name": "Deck 2",
                    "faction": "Monsters",
                    "leaderAbility": "Ability 2",
                    "provisionLimit": 160,
                    "categories": "Swarm",
                    "cards": []
                  }
                ]
                """;

        Path testFile = tempDir.resolve("decks.json");
        Files.writeString(testFile, json);

        List<Deck> decks = processor.processDirectory(tempDir);

        assertNotNull(decks);
        assertEquals(2, decks.size());
    }

    @Test
    void testProcessDirectoryWithInvalidFile(@TempDir Path tempDir) throws IOException, InterruptedException {
        String validJson = """
                {
                  "name": "Valid Deck",
                  "faction": "Skellige",
                  "leaderAbility": "Test",
                  "provisionLimit": 150,
                  "categories": "Midrange",
                  "cards": []
                }
                """;

        String invalidJson = "{ invalid json }";

        Path validFile = tempDir.resolve("valid.json");
        Path invalidFile = tempDir.resolve("invalid.json");

        Files.writeString(validFile, validJson);
        Files.writeString(invalidFile, invalidJson);

        List<Deck> decks = processor.processDirectory(tempDir);
        assertNotNull(decks);
        assertEquals(1, decks.size());
        assertEquals("Valid Deck", decks.get(0).getName());
    }

    @Test
    void testDifferentThreadCounts(@TempDir Path tempDir) throws IOException, InterruptedException {
        for (int i = 1; i <= 10; i++) {
            String json = String.format("""
                    {
                      "name": "Deck %d",
                      "faction": "Nilfgaard",
                      "leaderAbility": "Ability",
                      "provisionLimit": 150,
                      "categories": "Control",
                      "cards": []
                    }
                    """, i);

            Path testFile = tempDir.resolve("deck" + i + ".json");
            Files.writeString(testFile, json);
        }
        for (int threads : new int[]{1, 2, 4, 8}) {
            FileProcessor testProcessor = new FileProcessor(threads);
            List<Deck> decks = testProcessor.processDirectory(tempDir);
            assertEquals(10, decks.size(), "Should process all files with " + threads + " threads");
        }
    }
}