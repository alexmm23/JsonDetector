package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
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
    private JButton loadCsvButton;
    private JTable csvTable;

    public JsonFormatterApp() {
        setTitle("JSON Formatter and CSV Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(new javax.swing.plaf.nimbus.NimbusLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());
        jsonTextPane = new JTextPane();
        jsonTextPane.setEditable(true);
        jsonTextPane.setFont(new Font("Consolas", Font.PLAIN, 14));
        jsonTextPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(jsonTextPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(128, 128, 128), 1));

        formatButton = createStyledButton("Indent JSON", new Color(85, 170, 255), Color.WHITE);
        loadFileButton = createStyledButton("Load JSON File", new Color(85, 170, 255), Color.WHITE);
        loadCsvButton = createStyledButton("Load CSV File", new Color(85, 170, 255), Color.WHITE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.add(loadFileButton);
        buttonPanel.add(loadCsvButton);
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

        loadCsvButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(JsonFormatterApp.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    loadCsvFromFile(file);
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

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
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
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
        }
    }

    private void loadCsvFromFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            DefaultTableModel model = new DefaultTableModel();
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (isFirstLine) {
                    model.setColumnIdentifiers(values);
                    isFirstLine = false;
                } else {
                    model.addRow(values);
                }
            }
            csvTable = new JTable(model);
            JScrollPane tableScrollPane = new JScrollPane(csvTable);
            tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(128, 128, 128), 1));
            add(tableScrollPane, BorderLayout.EAST);
            revalidate();
            repaint();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
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
                throw new JSONException("Invalid JSON format");
            }
            applyColoredText(formattedJson);
        } catch (JSONException e) {
            JOptionPane.showMessageDialog(this, "Invalid JSON: " + e.getMessage());
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