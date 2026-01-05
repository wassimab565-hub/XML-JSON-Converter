package converter;

import org.json.JSONObject;
import org.json.XML;

import javax.xml.parsers.DocumentBuilder;

import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;

public class ApiConverter {

    // XML -> JSON
	public static String xmlToJson(String xml) throws Exception {
		JSONObject json = XML.toJSONObject(xml);
        return json.toString(4);
    }

    // JSON -> XML
	public static String jsonToXml(String json) throws Exception {
		JSONObject jsonObject = new JSONObject(json);

		String rawXml = XML.toString(jsonObject);

		return prettyFormatXml(rawXml);
    }

	private static String prettyFormatXml(String xmlInput) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(
        		new InputSource(new StringReader("<root>" + xmlInput + "</root>"))
        );

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        return writer.toString()
                .replaceFirst("<root>", "")
                .replaceFirst("</root>", "")
                .trim();
    }
}