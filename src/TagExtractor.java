import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class TagExtractor extends JFrame {

    private JTextField inputFileField, stopWordsFileField;
    private JTextArea outputArea;
    private JButton chooseInputFileButton, chooseStopWordsFileButton, extractButton, saveButton;
    private Map<String, Integer> tagFrequency;
    private Set<String> stopWords;

    public TagExtractor() {
        setTitle("Tag/Keyword Extractor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(3, 3));
        topPanel.add(new JLabel("Input File:"));
        inputFileField = new JTextField();
        topPanel.add(inputFileField);
        chooseInputFileButton = new JButton("Choose File");
        topPanel.add(chooseInputFileButton);

        topPanel.add(new JLabel("Stop Words File:"));
        stopWordsFileField = new JTextField();
        topPanel.add(stopWordsFileField);
        chooseStopWordsFileButton = new JButton("Choose File");
        topPanel.add(chooseStopWordsFileButton);

        extractButton = new JButton("Extract Tags");
        saveButton = new JButton("Save Tags");
        topPanel.add(extractButton);
        topPanel.add(saveButton);
        add(topPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        chooseInputFileButton.addActionListener(e -> chooseFile(inputFileField));
        chooseStopWordsFileButton.addActionListener(e -> chooseFile(stopWordsFileField));
        extractButton.addActionListener(e -> extractTags());
        saveButton.addActionListener(e -> saveTags());
    }

    private void chooseFile(JTextField textField) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void extractTags() {
        stopWords = FileUtils.loadStopWords(stopWordsFileField.getText());
        tagFrequency = new TreeMap<>();

        try {
            String content = FileUtils.readFile(inputFileField.getText());
            String[] words = content.split("\\s+");
            for (String word : words) {
                word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
                if (!word.isEmpty() && !stopWords.contains(word)) {
                    tagFrequency.put(word, tagFrequency.getOrDefault(word, 0) + 1);
                }
            }
            displayTags();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading input file: " + e.getMessage());
        }
    }

    private void displayTags() {
        StringBuilder sb = new StringBuilder();
        sb.append("File: ").append(inputFileField.getText()).append("\n\n");
        sb.append("Tags and Frequencies:\n");
        for (Map.Entry<String, Integer> entry : tagFrequency.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        outputArea.setText(sb.toString());
    }

    private void saveTags() {
        if (tagFrequency == null || tagFrequency.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tags to save. Please extract tags first.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileUtils.saveTags(file, tagFrequency);
                JOptionPane.showMessageDialog(this, "Tags saved successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving tags: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TagExtractor().setVisible(true));
    }
}