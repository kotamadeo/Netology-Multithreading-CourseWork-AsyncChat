package com.gmail.at.kotamadeo.server;

import com.gmail.at.kotamadeo.client.ClientGuiView;
import com.gmail.at.kotamadeo.database.SQLService;
import com.gmail.at.kotamadeo.util.Constants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;


public class ServerGuiView extends JFrame {

    private final ServerGuiController server;

    private JButton buttonStartServer;
    private JButton buttonStopServer;
    private JTextArea textAreaLog;

    public ServerGuiView(ServerGuiController server) {
        this.server = server;
        SQLService.getInstance();
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientGuiView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    protected void initComponents() {
        buttonStartServer = new JButton();
        buttonStopServer = new JButton();
        JScrollPane scrollPanel = new JScrollPane();
        textAreaLog = new JTextArea();
        JButton buttonSaveLog = new JButton();

        setTitle(Constants.SERVER_TITLE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(Constants.SERVER_SIZE_WIDTH, Constants.SERVER_SIZE_HEIGHT));
        setPreferredSize(new java.awt.Dimension(Constants.SERVER_SIZE_WIDTH, Constants.SERVER_SIZE_HEIGHT));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                server.stopServer();
                System.exit(0);
            }
        });
        setLocationRelativeTo(null);

        try {
            setIconImage(ImageIO.read(new File(Constants.SERVER_ICON_IMAGE)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        buttonStartServer.setIcon(new ImageIcon(Constants.IMAGE_ICON_START_SERVER));
        buttonStartServer.setText("Запустить сервер");
        buttonStartServer.addActionListener(e -> {
            server.startServer(getPortFromOptionPane());
            if (server.isServerStart()) {
                buttonStartServer.setEnabled(false);
                buttonStopServer.setEnabled(true);
            }
        });

        buttonStopServer.setIcon(new ImageIcon(Constants.IMAGE_ICON_STOP_SERVER));
        buttonStopServer.setText("Остановить сервер");
        buttonStopServer.setEnabled(false);
        buttonStopServer.addActionListener(e -> {
            server.stopServer();
            if (!server.isServerStart()) {
                buttonStartServer.setEnabled(true);
                buttonStopServer.setEnabled(false);
            }
        });
        buttonSaveLog.setIcon(new ImageIcon(Constants.IMAGE_ICON_SAVE_LOG));
        buttonSaveLog.setText("Сохранить логи");
        buttonSaveLog.addActionListener(e -> saveToFile());
        textAreaLog.setEditable(false);
        textAreaLog.setColumns(Constants.TEXT_AREA_LOG_SERVER_COLUMNS);
        textAreaLog.setRows(Constants.TEXT_AREA_LOG_SERVER_ROWS);
        scrollPanel.setViewportView(textAreaLog);
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(buttonStartServer, GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                                .addGap(0, 0, 0)
                                                .addComponent(buttonStopServer, GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                                .addGap(0, 0, 0)
                                                .addComponent(buttonSaveLog, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
                                        .addComponent(scrollPanel))
                                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(scrollPanel, GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(buttonStartServer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(buttonStopServer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(buttonSaveLog, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(5, 5, 5))
        );
        pack();
        setVisible(true);
    }

    public void refreshDialogWindowServer(String serviceMessage) {
        textAreaLog.append(serviceMessage);
    }

    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(new JButton("Сохранить")) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file != null) {
                if (!file.getName().toLowerCase().endsWith(".txt")) {
                    file = new File(file.getParentFile(), file.getName() + ".txt");
                }
                try {
                    textAreaLog.write(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected int getPortFromOptionPane() {
        while (true) {
            String port = JOptionPane.showInputDialog(this, "Введите порт сервера:", "Ввод порта сервера", JOptionPane.QUESTION_MESSAGE);
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Неверный ввод порта сервера. Попробуйте еще раз!", "Ошибка ввода порта сервера", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
