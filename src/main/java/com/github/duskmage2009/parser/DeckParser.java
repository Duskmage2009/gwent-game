package com.github.duskmage2009.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.duskmage2009.model.Deck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for JSON files containing Gwent deck data.
 * Uses TRUE streaming approach to avoid loading large files into memory.
 */
public class DeckParser {
    private static final Logger log = LoggerFactory.getLogger(DeckParser.class);
    private final ObjectMapper objectMapper;

    public DeckParser() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Parses a single JSON file into a Deck object using streaming.
     * Handles both single deck objects and arrays of decks.
     *
     * @param filePath path to JSON file
     * @return list of parsed Deck objects
     * @throws IOException if file reading or parsing fails
     */
    public List<Deck> parse(Path filePath) throws IOException {
        log.debug("Parsing deck(s) from file using streaming: {}", filePath);

        List<Deck> decks = new ArrayList<>();

        try (JsonParser jsonParser = objectMapper.getFactory().createParser(filePath.toFile())) {
            // Peek at first token to determine if it's an array or single object
            jsonParser.nextToken();

            if (jsonParser.isExpectedStartArrayToken()) {
                // File contains an array of decks - use streaming iterator
                log.debug("Detected array format, using streaming iterator");
                decks = parseArrayStreaming(filePath);
            } else {
                // File contains a single deck object
                log.debug("Detected single object format");
                Deck deck = objectMapper.readValue(jsonParser, Deck.class);
                decks.add(deck);
                log.info("Successfully parsed single deck: {} with {} cards",
                        deck.getName(), deck.getCards().size());
            }
        } catch (IOException e) {
            log.error("Failed to parse deck from file: {}", filePath, e);
            throw e;
        }

        return decks;
    }

    /**
     * Parses array of decks using MappingIterator for true streaming.
     * This approach never loads the entire file into memory.
     */
    private List<Deck> parseArrayStreaming(Path filePath) throws IOException {
        List<Deck> decks = new ArrayList<>();

        try (MappingIterator<Deck> iterator = objectMapper
                .readerFor(Deck.class)
                .readValues(filePath.toFile())) {

            while (iterator.hasNext()) {
                Deck deck = iterator.next();
                decks.add(deck);
                log.debug("Streamed deck: {} with {} cards",
                        deck.getName(), deck.getCards().size());
            }

            log.info("Successfully streamed {} decks from file", decks.size());
        }

        return decks;
    }

    /**
     * Alternative method: parse single deck using streaming JsonParser.
     * Use this for files that are known to contain single deck objects.
     */
    public Deck parseSingle(Path filePath) throws IOException {
        log.debug("Parsing single deck from file using streaming: {}", filePath);

        try (JsonParser jsonParser = objectMapper.getFactory().createParser(filePath.toFile())) {
            Deck deck = objectMapper.readValue(jsonParser, Deck.class);
            log.info("Successfully parsed deck: {} with {} cards",
                    deck.getName(), deck.getCards().size());
            return deck;
        } catch (IOException e) {
            log.error("Failed to parse deck from file: {}", filePath, e);
            throw e;
        }
    }
}