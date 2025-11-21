package com.github.duskmage2009.processor;

import com.github.duskmage2009.model.Deck;
import com.github.duskmage2009.parser.DeckParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class FileProcessor {
    private static final Logger log = LoggerFactory.getLogger(FileProcessor.class);
    private final DeckParser parser;
    private final int threadPoolSize;

    public FileProcessor(int threadPoolSize) {
        this.parser = new DeckParser();
        this.threadPoolSize = threadPoolSize;
    }

    public List<Deck> processDirectory(Path directoryPath) throws IOException, InterruptedException {
        log.info("Processing directory: {} with {} threads", directoryPath, threadPoolSize);

        List<Path> jsonFiles = findJsonFiles(directoryPath);
        log.info("Found {} JSON files", jsonFiles.size());

        if (jsonFiles.isEmpty()) {
            log.warn("No JSON files found in directory");
            return new ArrayList<>();
        }

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        List<Future<Deck>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (Path jsonFile : jsonFiles) {
            Future<Deck> future = executor.submit(() -> {
                try {
                    log.debug("Thread {} parsing file: {}",
                            Thread.currentThread().getName(), jsonFile.getFileName());
                    return parser.parse(jsonFile);
                } catch (IOException e) {
                    log.error("Failed to parse file: {}", jsonFile, e);
                    return null;
                }
            });
            futures.add(future);
        }

        List<Deck> decks = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (Future<Deck> future : futures) {
            try {
                Deck deck = future.get();
                if (deck != null) {
                    decks.add(deck);
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (ExecutionException e) {
                log.error("Error executing parsing task", e);
                failCount++;
            }
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("Processing completed: {} successful, {} failed in {} ms",
                successCount, failCount, duration);

        return decks;
    }

    private List<Path> findJsonFiles(Path directoryPath) throws IOException {
        List<Path> jsonFiles = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath, "*.json")) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    jsonFiles.add(entry);
                }
            }
        }

        return jsonFiles;
    }
}