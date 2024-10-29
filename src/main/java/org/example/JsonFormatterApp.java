package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JsonFormatterApp extends JFrame {

    private JTextPane jsonTextPane;
    private JButton formatButton;
    private JButton loadFileButton;

    public JsonFormatterApp() {
        setTitle("JSON Formatter");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        jsonTextPane = new JTextPane();
        jsonTextPane.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(jsonTextPane);

        formatButton = new JButton("Indentar JSON");
        loadFileButton = new JButton("Cargar archivo JSON");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loadFileButton);
        buttonPanel.add(formatButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(JsonFormatterApp.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    loadJsonFromFile(file);
                }
            }
        });

        formatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                formatAndColorJson();
            }
        });
    }

    private void loadJsonFromFile(File file) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line);
            }
            jsonTextPane.setText(contentBuilder.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
        }
    }

    private void formatAndColorJson() {
        String inputJson = jsonTextPane.getText().trim();
        try {
            String formattedJson;
            if (inputJson.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(inputJson);
                formattedJson = jsonObject.toString(4);
            } else if (inputJson.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(inputJson);
                formattedJson = jsonArray.toString(4);
            } else {
                throw new JSONException("Formato JSON inválido");
            }
            applyColoredText(formattedJson);
        } catch (JSONException e) {
            JOptionPane.showMessageDialog(this, "JSON no válido: " + e.getMessage());
        }
    }

    private void applyColoredText(String jsonText) {
        StyledDocument doc = jsonTextPane.getStyledDocument();
        StyleContext styleContext = StyleContext.getDefaultStyleContext();
        AttributeSet keyStyle = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLUE);
        AttributeSet textValueStyle = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(34, 139, 34));
        AttributeSet numberValueStyle = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.ORANGE);
        AttributeSet defaultStyle = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
        jsonTextPane.setText("");

        String[] tokens = jsonText.split("(?<=[:{},\\[\\]])|(?=[:{},\\[\\]])");

        for (String token : tokens) {
            try {
                if (token.trim().startsWith("\"") && token.trim().endsWith("\":")) {
                    doc.insertString(doc.getLength(), token, keyStyle);
                } else if (token.trim().startsWith("\"") && token.trim().endsWith("\"")) {
                    doc.insertString(doc.getLength(), token, textValueStyle);
                } else if (token.trim().matches("-?\\d+(\\.\\d+)?")) {
                    doc.insertString(doc.getLength(), token, numberValueStyle);
                } else {
                    doc.insertString(doc.getLength(), token, defaultStyle);
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JsonFormatterApp app = new JsonFormatterApp();
            app.setVisible(true);
        });
    }
}
