package crawler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler extends JFrame {
    List<Object[]> titleTableData;
    public WebCrawler() {
        super("Web Crawler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setVisible(true);
        setTitle("Web Crawler");

        titleTableData = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        JTable titleTable;
        JScrollPane titleTableScrollPane;
        String[] colNames = new String[]{"URL","Title"};
        DefaultTableModel titleTableModel = new DefaultTableModel(colNames,0);
        titleTable = new JTable(titleTableModel);
        titleTable.setEnabled(false);
        titleTableScrollPane = new JScrollPane(titleTable);
        titleTable.setFillsViewportHeight(true);
        titleTable.setName("TitlesTable");

        JTextField exportFileUrlTextField = new JTextField();
        exportFileUrlTextField.setName("ExportUrlTextField");

        JButton exportButton = new JButton("Export");
        exportButton.setName("ExportButton");
        exportButton.addActionListener(e->{
            String fileLocation = exportFileUrlTextField.getText();
            if(fileLocation.isEmpty())
                return;

            new SwingWorker<>(){

                @Override
                protected Object doInBackground(){
                    try(BufferedOutputStream bos = new BufferedOutputStream(
                            new FileOutputStream(new File(fileLocation))
                    )){
                        int size = titleTableModel.getRowCount();
                        for(int i=0;i<size;i++) {
                            bos.write(
                                    String.valueOf(titleTableModel.getValueAt(i,0)+"\n").getBytes());
                            bos.write(
                                    String.valueOf(titleTableModel.getValueAt(i,1)+"\n").getBytes());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        });
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
            titleTableData.clear();
            new SwingWorker<>() {
                String siteTitle;
                String siteSourceCode;
                @Override
                protected Object doInBackground(){
                    try(
                            InputStream is = new BufferedInputStream(new URL(url).openStream())
                            ){
                        siteSourceCode = new String(is.readAllBytes());
                        siteTitle=getTitle(siteSourceCode);
                        titleTableData.add(new String[]{url,siteTitle});
                        Pattern urlCapturePattern = Pattern.compile("<a.*href=['\"]([^'\"]*)['\"][^>]*>");
                        Matcher urlCaptureMatcher = urlCapturePattern.matcher(siteSourceCode);
                        while(urlCaptureMatcher.find()) {
                            String link = urlCaptureMatcher.group(1);
                            if(!link.contains("/")) {
                                link = url.substring(0,url.lastIndexOf('/')+1) + link;
                            }else if(!link.startsWith("http")) {
                                link = url.substring(0,url.indexOf(':')+1) +link;
                            }
                            URLConnection urlConnection = new URL(link).openConnection();
                            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
                            try(InputStream crawledUrlStream = urlConnection.getInputStream()) {
                                if (urlConnection.getContentType() == null) {
                                    titleTableData.add(new String[]{link, ""});
                                } else if (urlConnection.getContentType().contains("text/html") &&
                                        crawledUrlStream.available() > 0) {
                                    String title = getTitle(new String(urlConnection.getInputStream().readAllBytes()));
                                    titleTableData.add(new String[]{link, title});

                                }
                            } catch (FileNotFoundException fnfe){
                                fnfe.printStackTrace();
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    titleTableModel.setRowCount(0);
                    htmlTextArea.setText(siteSourceCode);
                    titleLabel.setText(siteTitle);
                    for(Object[] object : titleTableData) {
                        titleTableModel.addRow(object);
                    }
                }
            }.execute();

        };
        runButton.addActionListener(runActionListener);
        setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));


        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.X_AXIS));
        topPanel.add(urlTextField);
        topPanel.add(runButton);
        add(topPanel);
        add(titleLabel);
        add(titleTableScrollPane);
        add(exportFileUrlTextField);
        add(exportButton);
        //add(htmlTextArea);


    }

    private String getTitle(String siteSourceCode) {
        Pattern pattern = Pattern.compile("(<title>)(.*)(</title>)");
        Matcher matcher = pattern.matcher(siteSourceCode);
        if(matcher.find()){
            return matcher.group(2);
        }
        return "No Title Tag";
    }
}