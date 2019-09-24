package one.goranson.udpserver.swing;

import one.goranson.udpserver.common.UdpServer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class SwingApplication {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingApplication::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        final JFrame frame = new JFrame("UDP Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(800, 600));

        final ConsoleWindow textArea = new ConsoleWindow();
        mainPanel.add(textArea, BorderLayout.CENTER);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        final UdpServer udpServer = new UdpServer(textArea);
        textArea.setUdpServer(udpServer);
        udpServer.start();
    }
}
