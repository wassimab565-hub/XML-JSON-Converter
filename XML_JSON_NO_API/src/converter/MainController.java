package converter;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class MainController {

    @FXML
    private TextArea inputArea;

    @FXML
    private TextArea outputArea;

    @FXML
    private Label inputLabel;

    @FXML
    private Label outputLabel;

    // Current state : true = XML -> JSON | false = JSON -> XML
    private boolean xmlToJson = true;

    // Convert
    @FXML
    private void handleConvert() {

        String input = inputArea.getText();

        if (input == null || input.isBlank()) {
            outputArea.setText("Please enter input first.");
            return;
        }

        try {
            if (xmlToJson) {
        
                if (!looksLikeXml(input)) {
                    outputArea.setText("Invalid XML format.");
                    return;
                }

                String result = ManualConverter.xmlToJson(input);
                outputArea.setText(result);

            } else {
      
                if (!looksLikeJson(input)) {
                    outputArea.setText("Invalid JSON format.");
                    return;
                }

                String result = ManualConverter.jsonToXml(input);
                outputArea.setText(result);
            }

        } catch (Exception e) {
        	
            outputArea.setText("Conversion error.");
        }
    }

    // Inverse
    @FXML
    private void handleInverse() {
        xmlToJson = !xmlToJson;

        if (xmlToJson) {
            inputLabel.setText("XML");
            outputLabel.setText("JSON");
        } else {
            inputLabel.setText("JSON");
            outputLabel.setText("XML");
        }

        // TextArea
        outputArea.clear();
    }

    private boolean looksLikeXml(String text) {
        String t = text.trim();
        return t.startsWith("<") && t.endsWith(">");
    }

    private boolean looksLikeJson(String text) {
        String t = text.trim();
        return t.startsWith("{") || t.startsWith("[");
    }
}