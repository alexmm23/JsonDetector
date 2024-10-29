package org.example;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JsonFormatterApp extends JFrame {
    private JTextArea jsonTextArea;
    private JButton formatButton;
    private JButton loadFileButton;

    public JsonFormatterApp() {
        setTitle("JSON Formatter");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        jsonTextArea = new JTextArea();
        jsonTextArea.setLineWrap(true);
        jsonTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(jsonTextArea);

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
                formatJson();
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
            jsonTextArea.setText(contentBuilder.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
        }
    }

    private void formatJson() {
        String inputJson = jsonTextArea.getText().trim();
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
            jsonTextArea.setText(formattedJson);
        } catch (JSONException e) {
            JOptionPane.showMessageDialog(this, "JSON no válido: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JsonFormatterApp app = new JsonFormatterApp();
            app.setVisible(true);
        });
    }
}