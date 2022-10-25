package com.gmail.at.kotamadeo.client;

import com.gmail.at.kotamadeo.authorization.Login;
import com.gmail.at.kotamadeo.authorization.Registration;
import com.gmail.at.kotamadeo.database.SQLService;
import com.gmail.at.kotamadeo.util.Constants;
import com.gmail.at.kotamadeo.util.sound.MakeSound;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class ClientGuiView extends JFrame {

    private final ClientGuiController client;

    private JButton buttonChangeName;
    private JButton buttonConnectionToServer;
    private JButton buttonDisconnectToServer;
    private JButton buttonRegistration;
    private JButton buttonSend;
    private JButton buttonSignIn;
    private JButton buttonSignOut;
    private JButton buttonSoundOptions;
    private JList<String> listUserOnline;
    private JRadioButton radioButtonSendMessageToAll;
    private JRadioButton radioButtonSendPrivateMessageToSelectedUser;
    private JTextArea textAreaChatLog;
    private JTextField textFieldUserInputMessage;

    private boolean radioButtonCheckPrivateOrNot;

    public ClientGuiView(ClientGuiController clientGuiController) {
        this.client = clientGuiController;

        SQLService.getInstance();

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientGuiView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    protected void initComponents() {
        ButtonGroup buttonGroup = new ButtonGroup();
        radioButtonSendMessageToAll = new JRadioButton();
        radioButtonSendPrivateMessageToSelectedUser = new JRadioButton();
        buttonChangeName = new JButton();
        JButton buttonChangeInputColor = new JButton();
        buttonSoundOptions = new JButton();
        JButton buttonChatLog = new JButton();
        JButton buttonMoveToSystemTray = new JButton();
        buttonSend = new JButton();
        textFieldUserInputMessage = new JTextField();
        JScrollPane scrollPanelForUserListOnline = new JScrollPane();
        listUserOnline = new JList<>();
        buttonConnectionToServer = new JButton();
        JScrollPane scrollPanelForChatLog = new JScrollPane();
        textAreaChatLog = new JTextArea();
        buttonRegistration = new JButton();
        buttonSignIn = new JButton();
        buttonSignOut = new JButton();
        buttonDisconnectToServer = new JButton();
        setTitle(Constants.CLIENT_TITLE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(Constants.CLIENT_SIZE_WIDTH, Constants.CLIENT_SIZE_HEIGHT));
        setPreferredSize(new java.awt.Dimension(Constants.CLIENT_SIZE_WIDTH, Constants.CLIENT_SIZE_HEIGHT));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client.isClientConnected()) {
                    client.disableClient();
                }
                try {
                    if (SQLService.isConnected()) {
                        SQLService.closeConnection();
                    }
                } catch (SQLException sqlException) {
                    errorDialogWindow(sqlException.getMessage());
                }
                System.exit(0);
            }
        });
        setLocationRelativeTo(null);
        try {
            setIconImage(ImageIO.read(new File(Constants.CLIENT_ICON_IMAGE)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        buttonGroup.add(radioButtonSendMessageToAll);
        radioButtonSendMessageToAll.setSelected(true);
        radioButtonSendMessageToAll.setEnabled(false);
        radioButtonSendMessageToAll.setText("Отправить сообщение");
        radioButtonSendMessageToAll.setToolTipText("Отправить сообщение всем");
        radioButtonSendMessageToAll.addActionListener(e -> radioButtonCheckPrivateOrNot = false);
        buttonGroup.add(radioButtonSendPrivateMessageToSelectedUser);
        radioButtonSendPrivateMessageToSelectedUser.setText("Отправить приватное сообщение");
        radioButtonSendPrivateMessageToSelectedUser.setToolTipText("Отправить приватное сообщение выбранному пользователю");
        radioButtonSendPrivateMessageToSelectedUser.setEnabled(false);
        radioButtonSendPrivateMessageToSelectedUser.addActionListener(e -> radioButtonCheckPrivateOrNot = true);
        buttonChangeName.setIcon(new ImageIcon(Constants.IMAGE_ICON_CHANGE_NAME));
        buttonChangeName.setToolTipText("Изменить ник");
        buttonChangeName.setEnabled(false);
        buttonChangeName.addActionListener(e -> client.changeNickname());
        buttonChangeInputColor.setIcon(new ImageIcon(Constants.IMAGE_ICON_COLOR_WHEEL));
        buttonChangeInputColor.setToolTipText("Изменить цвет сообщений");
        buttonChangeInputColor.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "Настройка цвета", textAreaChatLog.getForeground());
            if (color != null) {
                textAreaChatLog.setForeground(color);
            }
        });

        buttonSoundOptions.setIcon(new ImageIcon(Constants.IMAGE_ICON_VOLUME_ON));
        buttonSoundOptions.setToolTipText("Настройки звуковых оповещений");
        buttonSoundOptions.addActionListener(e -> {
            if (!MakeSound.isIncluded()) {
                MakeSound.off();
                buttonSoundOptions.setIcon(new ImageIcon(Constants.IMAGE_ICON_VOLUME_OFF));
            } else {
                MakeSound.on();
                buttonSoundOptions.setIcon(new ImageIcon(Constants.IMAGE_ICON_VOLUME_ON));
            }
        });

        buttonChatLog.setIcon(new ImageIcon(Constants.IMAGE_ICON_SAVE_LOG));
        buttonChatLog.setToolTipText("Логи чата");
        buttonChatLog.addActionListener(e -> saveToFile());
        buttonMoveToSystemTray.setIcon(new ImageIcon(Constants.IMAGE_ICON_MOVE_TRAY));
        buttonMoveToSystemTray.setToolTipText("Свернуть в трей");
        buttonMoveToSystemTray.addActionListener(e -> moveToSystemTray());
        buttonSend.setIcon(new ImageIcon(Constants.IMAGE_ICON_SEND_MESSAGE));
        buttonSend.setText("Отправить");
        buttonSend.setToolTipText("Отправить сообщение");
        buttonSend.setEnabled(false);
        buttonSend.addActionListener(e -> {
            if (!textFieldUserInputMessage.getText().equals("")) {
                if (radioButtonCheckPrivateOrNot) {
                    if (listUserOnline.isSelectedIndex(listUserOnline.getSelectedIndex())) {
                        client.sendPrivateMessageOnServer(listUserOnline.getSelectedValue(), textFieldUserInputMessage.getText());
                    } else {
                        errorDialogWindow("Выберите пользователя из списка для отправки приватного сообщения");
                    }
                } else {
                    client.sendMessageOnServer(textFieldUserInputMessage.getText());
                }
                textFieldUserInputMessage.setText("");
            }
        });

        textFieldUserInputMessage.setToolTipText("Введите сообщение");
        textFieldUserInputMessage.setEnabled(false);
        textFieldUserInputMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!textFieldUserInputMessage.getText().equals("") && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (radioButtonCheckPrivateOrNot) {
                        if (listUserOnline.isSelectedIndex(listUserOnline.getSelectedIndex())) {
                            client.sendPrivateMessageOnServer(listUserOnline.getSelectedValue(), textFieldUserInputMessage.getText());
                        } else {
                            errorDialogWindow("Выберите пользователя из списка для отправки приватного сообщения");
                        }
                    } else {
                        client.sendMessageOnServer(textFieldUserInputMessage.getText());
                    }
                    textFieldUserInputMessage.setText("");
                }
            }
        });

        listUserOnline.setToolTipText("Список пользователей онлайн");
        listUserOnline.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPanelForUserListOnline.setViewportView(listUserOnline);

        textAreaChatLog.setEditable(false);
        textAreaChatLog.setColumns(Constants.TEXT_AREA_CHAT_LOG_COLUMNS);
        textAreaChatLog.setRows(Constants.TEXT_AREA_CHAT_LOG_ROWS);
        textAreaChatLog.setToolTipText("Логи чата");
        textAreaChatLog.setFont(new Font(Constants.TEXT_AREA_CHAT_LOG_FONT_NAME, Font.PLAIN, Constants.TEXT_AREA_CHAT_LOG_FONT_SIZE));
        scrollPanelForChatLog.setViewportView(textAreaChatLog);

        buttonRegistration.setIcon(new ImageIcon(Constants.IMAGE_ICON_REGISTRATION));
        buttonRegistration.setToolTipText("Регистрация в чате");
        buttonRegistration.addActionListener(e -> {
            if (!client.isDatabaseConnected()) {
                Registration registration = new Registration(this);
                registration.setVisible(true);
                if (registration.isSucceeded()) {
                    client.setNickname(registration.getNickname());
                }
            }
        });
        buttonSignIn.setIcon(new ImageIcon(Constants.IMAGE_ICON_SIGN_IN));
        buttonSignIn.setToolTipText("Авторизация в чате");
        buttonSignIn.addActionListener(e -> {
            if (!client.isDatabaseConnected()) {
                Login loginDialog = new Login(this);
                loginDialog.setVisible(true);
                if (loginDialog.isSucceeded()) {
                    client.setNickname(loginDialog.getNickname());
                    client.setDatabaseConnected(true);
                    buttonSignIn.setEnabled(false);
                    buttonSignOut.setEnabled(true);
                    buttonConnectionToServer.setEnabled(true);
                    buttonRegistration.setEnabled(false);
                }
            }
        });
        buttonSignOut.setIcon(new ImageIcon(Constants.IMAGE_ICON_SIGN_OUT));
        buttonSignOut.setToolTipText("Выход из чата");
        buttonSignOut.setEnabled(false);
        buttonSignOut.addActionListener(e -> {
            if (client.isDatabaseConnected()) {
                client.setDatabaseConnected(false);
                buttonSignOut.setEnabled(false);
                buttonSignIn.setEnabled(true);
                radioButtonSendMessageToAll.setEnabled(false);
                radioButtonSendPrivateMessageToSelectedUser.setEnabled(false);
                buttonSend.setEnabled(false);
                buttonConnectionToServer.setEnabled(false);
                buttonDisconnectToServer.setEnabled(false);
                buttonRegistration.setEnabled(true);
                buttonChangeName.setEnabled(false);
                if (client.isClientConnected()) {
                    client.disableClient();
                }
            }
        });
        buttonConnectionToServer.setIcon(new ImageIcon(Constants.IMAGE_ICON_CONNECTION));
        buttonConnectionToServer.setText("Подключиться");
        buttonConnectionToServer.setToolTipText("Подключиться к серверу");
        buttonConnectionToServer.setEnabled(false);
        buttonConnectionToServer.addActionListener(e -> {
            if (client.isDatabaseConnected()) {
                client.connectToServer();
                if (client.isClientConnected()) {
                    buttonDisconnectToServer.setEnabled(true);
                    buttonConnectionToServer.setEnabled(false);
                    buttonChangeName.setEnabled(true);
                    textFieldUserInputMessage.setEnabled(true);
                    radioButtonSendMessageToAll.setEnabled(true);
                    radioButtonSendPrivateMessageToSelectedUser.setEnabled(true);
                    buttonSend.setEnabled(true);
                }
            }
        });
        buttonDisconnectToServer.setIcon(new ImageIcon(Constants.IMAGE_ICON_DISCONNECT));
        buttonDisconnectToServer.setText("Отсоединиться");
        buttonDisconnectToServer.setToolTipText("Отсоединиться от сервера");
        buttonDisconnectToServer.setEnabled(false);
        buttonDisconnectToServer.addActionListener(e -> {
            if (client.isClientConnected()) {
                client.disableClient();
                if (!client.isClientConnected()) {
                    buttonConnectionToServer.setEnabled(true);
                    buttonDisconnectToServer.setEnabled(false);
                    buttonChangeName.setEnabled(false);
                    textFieldUserInputMessage.setEnabled(false);
                    radioButtonSendMessageToAll.setEnabled(false);
                    radioButtonSendPrivateMessageToSelectedUser.setEnabled(false);
                    buttonSend.setEnabled(false);
                }
            }
        });
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(radioButtonSendMessageToAll)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(radioButtonSendPrivateMessageToSelectedUser)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(buttonSignIn)
                                                .addGap(0, 0, 0)
                                                .addComponent(buttonSignOut)
                                                .addGap(0, 0, 0)
                                                .addComponent(buttonRegistration)
                                                .addGap(0, 0, 0)
                                                .addComponent(buttonChangeName, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, 0)
                                                .addComponent(buttonChangeInputColor, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, 0)
                                                .addComponent(buttonSoundOptions, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, 0)
                                                .addComponent(buttonChatLog, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, 0)
                                                .addComponent(buttonMoveToSystemTray, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, 0)
                                                .addComponent(buttonSend, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addComponent(textFieldUserInputMessage)
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(scrollPanelForChatLog)
                                                .addGap(5, 5, 5)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(buttonDisconnectToServer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(buttonConnectionToServer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(scrollPanelForUserListOnline, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE))))
                                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(buttonConnectionToServer, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(buttonDisconnectToServer)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(scrollPanelForUserListOnline, GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
                                        .addComponent(scrollPanelForChatLog))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldUserInputMessage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(buttonChangeInputColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buttonSoundOptions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(radioButtonSendMessageToAll)
                                                .addComponent(radioButtonSendPrivateMessageToSelectedUser)
                                                .addComponent(buttonSend))
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(buttonRegistration)
                                                .addComponent(buttonSignIn)
                                                .addComponent(buttonSignOut))
                                        .addComponent(buttonChatLog, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buttonMoveToSystemTray, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buttonChangeName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5))
        );
        pack();
        setVisible(true);
    }

    protected void addMessage(String text) {
        textAreaChatLog.append("[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + text);
        MakeSound.playSound(Constants.SOUND_URL_NEW_MESSAGE);
    }

    protected void refreshListUsers(Set<String> allUserNicknames) {
        StringBuilder text = new StringBuilder();
        for (String user : allUserNicknames) {
            text.append(user).append("\n");
        }
        String[] strings = text.toString().split("\n");
        listUserOnline.setModel(new AbstractListModel<>() {
            public int getSize() {
                return allUserNicknames.size();
            }

            public String getElementAt(int i) {
                return strings[i];
            }
        });
    }

    protected String getServerAddress() {
        while (true) {
            return JOptionPane.showInputDialog(this, "Введите адрес сервера:", "Адрес порта сервера:", JOptionPane.QUESTION_MESSAGE).trim();
        }
    }

    protected int getPort() {
        while (true) {
            String port = JOptionPane.showInputDialog(this, "Введите порт сервера:", "Порта сервера:", JOptionPane.QUESTION_MESSAGE);
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Неверный адрес порта сервера. Попробуйте еще раз!", "Ошибка ввода адреса сервера", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    protected String getNickname() {
        return JOptionPane.showInputDialog(this, "Введите логин:", "Логин", JOptionPane.QUESTION_MESSAGE);
    }

    protected void errorDialogWindow(String text) {

        JOptionPane.showMessageDialog(this, text, "Ошибка", JOptionPane.ERROR_MESSAGE);
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
                    textAreaChatLog.write(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void moveToSystemTray() {
        try {
            BufferedImage icon = ImageIO.read(new File(Constants.CLIENT_ICON_IMAGE));
            final TrayIcon trayIcon = new TrayIcon(icon, Constants.CLIENT_TITLE);
            setVisible(false);
            SystemTray systemTray = SystemTray.getSystemTray();
            systemTray.add(trayIcon);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        setVisible(true);
                        setExtendedState(Frame.NORMAL);
                        systemTray.remove(trayIcon);
                    }
                }
            });
        } catch (IOException | AWTException e) {
            errorDialogWindow(e.getMessage());
        }
    }
}
