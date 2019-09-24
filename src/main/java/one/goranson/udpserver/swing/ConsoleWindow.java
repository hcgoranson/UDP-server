package one.goranson.udpserver.swing;

import lombok.Setter;
import one.goranson.udpserver.common.UdpServer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.util.function.Consumer;

public class ConsoleWindow extends JPanel implements Consumer<String> {
    private final JTextArea textArea;
    private final JTextField filter;
    private boolean autoScroll = true;
    private final Highlighter highlighter;
    private final Highlighter.HighlightPainter painter;

    private String filterText = "";

    @Setter
    private UdpServer udpServer;

    public ConsoleWindow() {
        super(new BorderLayout());

        // The log area
        textArea = new JTextArea();
        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        textArea.setForeground(Color.GREEN);
        textArea.setBackground(Color.BLACK);
        textArea.setEditable(true);

        printHeader();

        highlighter = textArea.getHighlighter();
        painter = new DefaultHighlighter.DefaultHighlightPainter(Color.gray);

        // Wrap log area inside a scroll panel
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setAutoscrolls(false);
        add(scroll);

        // Add a toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.setLayout(new FlowLayout(SwingConstants.LEADING));
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.SOUTH);

        // ...and some buttons
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(event -> {
            textArea.setText("");
            printHeader();
        });
        toolBar.add(clearButton);

        JCheckBox timestampCheckBox = new JCheckBox("Timestamp");
        timestampCheckBox.setSelected(true);
        timestampCheckBox
                .addItemListener(event -> udpServer.setAddTimestamp(event.getStateChange() == ItemEvent.SELECTED));
        toolBar.add(timestampCheckBox);

        JCheckBox autoScrollCheckBox = new JCheckBox("Autoscroll");
        autoScrollCheckBox.setSelected(true);
        autoScrollCheckBox
                .addItemListener(event -> autoScroll = event.getStateChange() == ItemEvent.SELECTED);
        toolBar.add(autoScrollCheckBox);

        filter = new JTextField(10);
        filter.addActionListener(event -> {
            highlighter.removeAllHighlights();

            filterText = filter.getText();

            // Highlight the current text
            highlight(0);

            // Filter on incoming UDP packages
            udpServer.updateFilter(filterText);
        });
        toolBar.add(filter);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(event -> System.exit(1));
        toolBar.add(exitButton);
    }

    // This is not super nice...
    private void highlight(int endOfCurrentText) {
        String textToHighligt = filterText;
        if (textToHighligt != null && textToHighligt.length() > 0) {
            boolean continueSearching = true;
            do {
                int startIndex = textArea.getText().indexOf(textToHighligt, endOfCurrentText);
                int endIndex = startIndex + textToHighligt.length();
                if (startIndex != -1) {
                    try {
                        highlighter.addHighlight(startIndex, endIndex, painter);
                    } catch (BadLocationException e) {
                        System.out.println("start: " + startIndex + "| endIndex: " + endIndex);
                        e.printStackTrace();
                    }
                }
                endOfCurrentText = endIndex;
                if (startIndex == -1 || endIndex >= textArea.getText().length()) {
                    continueSearching = false;
                }
            }
            while (continueSearching);
        }
    }

    @Override
    public void accept(String message) {
        int endOfCurrentText = textArea.getText().length();
        this.textArea.append(message.concat(System.lineSeparator()));
        if (autoScroll) {
            textArea.setCaretPosition(textArea.getText().length());
        }

        highlight(endOfCurrentText);
    }

    private void printHeader() {
        String message =
                "      __    __   _______  .______           _______. _______ .______     ____    ____  _______ .______      \n" +
                        "     |  |  |  | |       \\ |   _  \\         /       ||   ____||   _  \\    \\   \\  /   / |   ____||   _  \\     \n" +
                        "     |  |  |  | |  .--.  ||  |_)  |       |   (----`|  |__   |  |_)  |    \\   \\/   /  |  |__   |  |_)  |    \n" +
                        "     |  |  |  | |  |  |  ||   ___/         \\   \\    |   __|  |      /      \\      /   |   __|  |      /     \n" +
                        "     |  `--'  | |  '--'  ||  |         .----)   |   |  |____ |  |\\  \\----.  \\    /    |  |____ |  |\\  \\----.\n" +
                        "      \\______/  |_______/ | _|         |_______/    |_______|| _| `._____|   \\__/     |_______|| _| `._____|\n" +
                        "                                                                                                       \n";
        String instructions =
                "     --------------------------------------------------------------------------------------------------\n" +
                        "     This server will listen for UDP packages on port 8125\n" +
                        "     To apply a filter, type in the keyword in the textfield below and press enter\n" +
                        "     This will filter on the incoming messages\n" +
                        "     --------------------------------------------------------------------------------------------------\n";
        textArea.append(message);
        textArea.append(instructions);
    }

}
