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


public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static final int DEFAULT_THREAD_COUNT = 4;

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }

        String directoryPath = args[0];
        String attribute = args[1];
        int threadCount = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_THREAD_COUNT;

        try {
            long startTime = System.currentTimeMillis();

            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║  Gwent Deck Statistics Generator      ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println();

            log.info("Starting Gwent Statistics Application");
            log.info("Directory: {}, Attribute: {}, Threads: {}", directoryPath, attribute, threadCount);

            System.out.println("📁 Processing JSON files from: " + directoryPath);
            System.out.println("🧵 Using " + threadCount + " threads");
            System.out.println();

            FileProcessor processor = new FileProcessor(threadCount);
            Path path = Paths.get(directoryPath);
            List<Deck> decks = processor.processDirectory(path);

            if (decks.isEmpty()) {
                log.warn("No decks found. Exiting.");
                System.out.println("⚠️  No decks found in directory!");
                return;
            }

            System.out.println("✅ Successfully loaded " + decks.size() + " decks");
            System.out.println();

            StatisticsCalculator calculator = new StatisticsCalculator();
            calculator.printSummary(decks);

            System.out.println("📊 Calculating statistics for attribute: " + attribute);
            Map<String, Integer> statistics = calculator.calculateStatistics(decks, attribute);

            System.out.println("\n🏆 Top 10 Results:");
            System.out.println("─────────────────────────────────────");
            int count = 0;
            for (Map.Entry<String, Integer> entry : statistics.entrySet()) {
                if (count++ >= 10) break;
                System.out.printf("%2d. %-30s : %d\n", count, entry.getKey(), entry.getValue());
            }
            System.out.println("─────────────────────────────────────");

            System.out.println("\n💾 Writing results to XML...");
            XmlStatisticsWriter writer = new XmlStatisticsWriter();
            writer.writeStatistics(statistics, attribute, directoryPath);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            System.out.println("\n✨ Processing completed successfully!");
            System.out.println("⏱️  Total time: " + duration + " ms");
            System.out.println("📄 Output file: statistics_by_" + attribute.toLowerCase() + ".xml");
            System.out.println();

            log.info("Processing completed successfully in {} ms", duration);

        } catch (IllegalArgumentException e) {
            log.error("Invalid attribute: {}", attribute);
            System.err.println("\n❌ Error: " + e.getMessage());
            System.err.println("\n💡 Supported attributes:");
            System.err.println("   • faction       - Card faction statistics");
            System.err.println("   • type          - Card type statistics");
            System.err.println("   • provision     - Provision cost statistics");
            System.err.println("   • power         - Card power statistics");
            System.err.println("   • leaderAbility - Leader ability statistics");
            System.err.println("   • totalPower    - Total deck power statistics");
            System.err.println("   • deckFaction   - Deck faction statistics");
            System.exit(1);
        } catch (Exception e) {
            log.error("Application error", e);
            System.err.println("\n❌ Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  Gwent Deck Statistics Generator      ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java -jar gwent-game.jar <directory_path> <attribute> [thread_count]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  directory_path  - Path to directory containing JSON deck files");
        System.out.println("  attribute       - Attribute to calculate statistics for");
        System.out.println("  thread_count    - Number of threads (default: 4)");
        System.out.println();
        System.out.println("Supported attributes:");
        System.out.println("  • faction       - Card faction statistics (Northern Realms, Monsters, etc.)");
        System.out.println("  • type          - Card type statistics (Unit, Special, Artifact, Stratagem)");
        System.out.println("  • provision     - Provision cost statistics");
        System.out.println("  • power         - Card power statistics (Unit cards only)");
        System.out.println("  • leaderAbility - Leader ability statistics");
        System.out.println("  • totalPower    - Total unit power per deck (grouped by ranges)");
        System.out.println("  • deckFaction   - Deck faction statistics");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar gwent-game.jar ./decks faction");
        System.out.println("  java -jar gwent-game.jar ./decks type 8");
        System.out.println("  java -jar gwent-game.jar ./decks totalPower 4");
        System.out.println();
    }
}