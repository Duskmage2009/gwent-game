package com.github.duskmage2009;


import com.github.duskmage2009.model.Deck;
import com.github.duskmage2009.output.XmlStatisticsWriter;
import com.github.duskmage2009.processor.FileProcessor;
import com.github.duskmage2009.statistics.StatisticsCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;


public class GwentStatisticsApp {
    private static final Logger log = LoggerFactory.getLogger(GwentStatisticsApp.class);
    private static final int DEFAULT_THREAD_COUNT = 4;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.exit(1);
        }

        String directoryPath = args[0];
        String attribute = args[1];
        int threadCount = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_THREAD_COUNT;

        try {
            long startTime = System.currentTimeMillis();


            log.info("Starting Gwent Statistics Application");
            log.info("Directory: {}, Attribute: {}, Threads: {}", directoryPath, attribute, threadCount);


            FileProcessor processor = new FileProcessor(threadCount);
            Path path = Paths.get(directoryPath);
            List<Deck> decks = processor.processDirectory(path);

            if (decks.isEmpty()) {
                log.warn("No decks found. Exiting.");
                return;
            }


            StatisticsCalculator calculator = new StatisticsCalculator();
            calculator.printSummary(decks);

            Map<String, Integer> statistics = calculator.calculateStatistics(decks, attribute);

            int count = 0;

            XmlStatisticsWriter writer = new XmlStatisticsWriter();
            writer.writeStatistics(statistics, attribute, directoryPath);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;


            log.info("Processing completed successfully in {} ms", duration);

        } catch (IllegalArgumentException e) {
            log.error("Invalid attribute: {}", attribute);
            System.exit(1);
        } catch (Exception e) {
            log.error("Application error", e);
            System.exit(1);
        }
    }

}