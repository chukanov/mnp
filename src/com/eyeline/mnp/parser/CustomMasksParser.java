package com.eyeline.mnp.parser;

import com.eyeline.mnp.mask.Mask;
import com.eyeline.utils.DirectoryWatchDog;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Parser for MNOs defined in XML
 * @author Chukanov
 */
public class CustomMasksParser extends DirectoryWatchDog implements MasksParser, MasksParser.WatchDog {
    private Path config;
    private Runnable onUpdate;

    public CustomMasksParser(Path config) {
        this.config = config;
        if (Files.isDirectory(config)) {
            throw new IllegalArgumentException("Path "+ config +" is a directory");
        }
    }

    @Override
    public void parse(Consumer<MnoInfo> consumer) throws Exception {
        File fXmlFile = config.toFile();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();
        XPathExpression xpathMno = XPathFactory.newInstance().newXPath().compile("//*[local-name()='mno']");
        XPathExpression xpathMasksMno = XPathFactory.newInstance().newXPath().compile("//*[local-name()='mask']");
        NodeList mnoNodes = (NodeList) xpathMno.evaluate(doc, XPathConstants.NODESET);
        for (int i=0; i<mnoNodes.getLength(); i++) {
            Node node = mnoNodes.item(i);
            NamedNodeMap attributes = node.getAttributes();
            String country = getValue(attributes.getNamedItem("country"));
            String title = getValue(attributes.getNamedItem("title"));
            String area = getValue(attributes.getNamedItem("area"));
            Set<Mask> masks = new HashSet<>();
            NodeList maskNodes = (NodeList) xpathMasksMno.evaluate(doc, XPathConstants.NODESET);
            for (int j=0; j<maskNodes.getLength(); j++) {
                masks.add(Mask.parse(getValue(maskNodes.item(j))));
            }
            consumer.accept(new MnoInfo(title, area, country, masks));
        }
    }

    private String getValue(Node node) {
        try{
            return node.getTextContent().trim();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    protected void processChangedFile(Path filePath) throws Exception {
        if (Files.isSameFile(config, filePath)) {
            onUpdate.run();
        }
    }

    @Override
    protected Path getWatchDir() {
        return config.getParent();
    }

    @Override
    public void watch(Runnable onUpdate) throws Exception {
        this.onUpdate = onUpdate;
        super.start();
    }
}
