package com.github.duskmage2009.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.duskmage2009.model.Deck;

import java.io.File;

public class DeckParser {
    ObjectMapper objectMapper = new ObjectMapper();
    File file = new File("Northern Realms.json");

    Deck deck = new Deck();
    

}
