package com.github.duskmage2009.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Map;


public class XmlStatisticsWriter {
    private static final Logger log = LoggerFactory.getLogger(XmlStatisticsWriter.class);


    public void writeStatistics(Map<String, Integer> statistics, String attribute, String outputDirectory)
            throws Exception {
        String filename = String.format("statistics_by_%s.xml", attribute.toLowerCase());
        File outputFile = new File(outputDirectory, filename);

        log.info("Writing statistics to file: {}", outputFile.getAbsolutePath());

        Document doc = createXmlDocument(statistics);
        writeDocumentToFile(doc, outputFile);

        log.info("Statistics successfully written to: {}", outputFile.getAbsolutePath());
        System.out.println("Statistics saved to: " + outputFile.getAbsolutePath());
    }

    private Document createXmlDocument(Map<String, Integer> statistics)
            throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("statistics");
        doc.appendChild(root);

        for (Map.Entry<String, Integer> entry : statistics.entrySet()) {
            Element item = doc.createElement("item");

            Element value = doc.createElement("value");
            value.setTextContent(entry.getKey());
            item.appendChild(value);

            Element count = doc.createElement("count");
            count.setTextContent(entry.getValue().toString());
            item.appendChild(count);

            root.appendChild(item);
        }

        return doc;
    }

    private void writeDocumentToFile(Document doc, File file) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }
}