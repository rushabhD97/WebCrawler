package crawler;

import javax.swing.*;

public class WebCrawler extends JFrame {
    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setVisible(true);
        setTitle("Simple Window");
        initComponents();
    }

    private void initComponents() {
        JTextArea jTextArea = new JTextArea();
        jTextArea.setName("TextArea");
        jTextArea.setText("HTML code?");
        jTextArea.setEnabled(false);
        add(jTextArea);
    }
}