package com.github.duskmage2009.parser;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.duskmage2009.model.Deck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.nio.file.Path;


public class DeckParser {
    private static final Logger log = LoggerFactory.getLogger(DeckParser.class);
    private final ObjectMapper objectMapper;

    public DeckParser() {
        this.objectMapper = new ObjectMapper();
    }


    public Deck parse(Path filePath) throws IOException {
        log.debug("Parsing deck from file: {}", filePath);

        try {
            Deck deck = objectMapper.readValue(filePath.toFile(), Deck.class);
            log.info("Successfully parsed deck: {} with {} cards",
                    deck.getName(), deck.getCards().size());
            return deck;
        } catch (IOException e) {
            log.error("Failed to parse deck from file: {}", filePath, e);
            throw e;
        }
    }
}