package com.gmail.at.kotamadeo.authorization;

import com.gmail.at.kotamadeo.database.SQLService;
import com.gmail.at.kotamadeo.settings.Settings;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.SQLException;

@Getter
public class Login extends JDialog {

    private final JTextField textFieldNickname;
    private final JPasswordField passwordField;
    private boolean succeeded;

    public Login(Frame parent) {
        super(parent, Settings.LOGIN_TITLE, true);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel labelNickname = new JLabel("Логин: ");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        panel.add(labelNickname, gridBagConstraints);
        textFieldNickname = new JTextField(20);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        panel.add(textFieldNickname, gridBagConstraints);
        JLabel labelPassword = new JLabel("Пароль: ");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        panel.add(labelPassword, gridBagConstraints);
        passwordField = new JPasswordField(20);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        panel.add(passwordField, gridBagConstraints);
        panel.setBorder(new LineBorder(Color.GRAY));
        JButton buttonLogin = new JButton("Авторизоваться");
        buttonLogin.addActionListener(e -> {
            try {
                if (authenticate(getNickname(), getPassword())) {
                    JOptionPane.showMessageDialog(Login.this, "Вы успешно авторизовались.", "Авторизация", JOptionPane.INFORMATION_MESSAGE);
                    succeeded = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(Login.this, "Неверный логин и/или пароль", "Авторизация", JOptionPane.ERROR_MESSAGE);
                    textFieldNickname.setText("");
                    passwordField.setText("");
                    succeeded = false;
                }
            } catch (SQLException sqlException) {
                JOptionPane.showMessageDialog(Login.this, sqlException.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton buttonCancel = new JButton("Отмена");
        buttonCancel.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buttonLogin);
        buttonPanel.add(buttonCancel);
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public boolean authenticate(String getNickname, String password) throws SQLException {
        return SQLService.getNicknameByLoginAndPassword(getNickname, password) != null;
    }

    public String getNickname() {
        return textFieldNickname.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }
}
