package crawler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        urlTextField.setName("UrlTextField");
        JButton runButton = new JButton("Parse");
        runButton.setName("RunButton");

        JLabel titleLabel = new JLabel("Title: ");
        titleLabel.setName("TitleLabel");

        ActionListener runActionListener = e -> {
            final String url = urlTextField.getText();
            try(

                    InputStream is = new BufferedInputStream(new URL(url).openStream())
            ){
                String siteSourceCode = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                htmlTextArea.setText(siteSourceCode);

                titleLabel.setText(getTitle(siteSourceCode));
                System.out.println(url + "\n" + siteSourceCode);
            }catch (IOException io){
                io.printStackTrace();
            }
        };
        runButton.addActionListener(runActionListener);
        setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.X_AXIS));
        topPanel.add(urlTextField);
        topPanel.add(runButton);
        add(topPanel);
        add(titleLabel);
        add(htmlTextArea);

    }

    private String getTitle(String siteSourceCode) {
        Pattern pattern = Pattern.compile("(<title>)(.*)(<\\/title>)");
        Matcher matcher = pattern.matcher(siteSourceCode);
        if(matcher.find()){
            return matcher.group(2);
        }
        return "No Title Tag";
    }
}