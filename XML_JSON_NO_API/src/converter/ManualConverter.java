package converter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class ManualConverter {

    // XML ➜ JSON
    public static String xmlToJson(String xmlContent) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.parse(
                new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8))
        );

        doc.getDocumentElement().normalize();

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        Element root = doc.getDocumentElement();
        json.append("  \"").append(root.getNodeName()).append("\": ");
        buildJsonFromElement(root, json, "  ");
        json.append("\n}");

        return json.toString();
    }

    private static void buildJsonFromElement(Element element, StringBuilder json, String indent) {

        NodeList children = element.getChildNodes();
        boolean hasElementChild = false;

        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                hasElementChild = true;
                break;
            }
        }

        if (!hasElementChild) {
            json.append("\"").append(element.getTextContent().trim()).append("\"");
            return;
        }

        json.append("{\n");
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element child = (Element) node;
                json.append(indent).append("  \"")
                    .append(child.getNodeName()).append("\": ");
                buildJsonFromElement(child, json, indent + "  ");
                json.append(",\n");
            }
        }

        json.setLength(json.length() - 2);
        json.append("\n").append(indent).append("}");
    }


    // JSON ➜ XML
    public static String jsonToXml(String jsonContent) {
        StringBuilder xml = new StringBuilder();
        buildXmlFromJson(jsonContent.trim(), xml, 0);
        return xml.toString();
    }

    private static void buildXmlFromJson(String json, StringBuilder xml, int depth) {

        json = json.trim();

        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1).trim();
        }

        int i = 0;
        while (i < json.length()) {

            if (json.charAt(i) == '"') {
                int keyStart = i + 1;
                int keyEnd = json.indexOf('"', keyStart);
                if (keyEnd == -1) break;

                String key = json.substring(keyStart, keyEnd);
                i = json.indexOf(':', keyEnd) + 1;

                while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;

                String indent = "    ".repeat(depth);

                if (i < json.length() && json.charAt(i) == '{') {
                    int brace = 1;
                    int valueStart = i++;
                    while (i < json.length() && brace > 0) {
                        if (json.charAt(i) == '{') brace++;
                        else if (json.charAt(i) == '}') brace--;
                        i++;
                    }

                    String value = json.substring(valueStart, i);

                    xml.append(indent).append("<").append(key).append(">\n");
                    buildXmlFromJson(value, xml, depth + 1);
                    xml.append(indent).append("</").append(key).append(">\n");

                } else {
                    int comma = json.indexOf(',', i);
                    if (comma == -1) comma = json.length();

                    String value = json.substring(i, comma)
                            .replaceAll("\"", "").trim();

                    xml.append(indent)
                       .append("<").append(key).append(">")
                       .append(value)
                       .append("</").append(key).append(">\n");

                    i = comma + 1;
                }
            } else {
                i++;
            }
        }
    }
}