package crawler;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebCrawler extends JFrame {
    public WebCrawler() {
        super("Web Crawler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setVisible(true);
        setTitle("Web Crawler");
        initComponents();
    }

    private void initComponents() {
        JTextArea htmlTextArea = new JTextArea();
        htmlTextArea.setName("HtmlTextArea");
        htmlTextArea.setEnabled(false);

        JTextField urlTextField = new JTextField();
        urlTextField.setBounds(20,20,100,20);
        urlTextField.setName("UrlTextField");

        JButton runButton = new JButton("Run");
        runButton.setName("RunButton");
        runButton.setBounds(150,20,50,20);
        ActionListener runActionListener = e -> {
            final String url = urlTextField.getText();
            try(

                    InputStream is = new BufferedInputStream(new URL(url).openStream())
            ){
                String siteSourceCode = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                htmlTextArea.setText(siteSourceCode);
                System.out.println(url + "\n" + siteSourceCode);
            }catch (IOException io){
                io.printStackTrace();
            }
        };
        runButton.addActionListener(runActionListener);

        add(htmlTextArea);
        add(urlTextField);
        add(runButton);
    }
}