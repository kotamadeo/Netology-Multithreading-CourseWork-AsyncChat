package com.gmail.at.kotamadeo.authorization;

import com.gmail.at.kotamadeo.database.SQLService;
import com.gmail.at.kotamadeo.util.Constants;
import com.gmail.at.kotamadeo.util.validator.Validator;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.SQLException;

@Getter
public class Registration extends JDialog {

    private final JTextField textFieldNickname;
    private final JPasswordField passwordField;
    private boolean succeeded;

    public Registration(Frame parent) {
        super(parent, Constants.REGISTRATION_TITLE, true);
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
        JButton buttonRegistration = new JButton("Зарегистрироваться");
        buttonRegistration.addActionListener(e -> {
            try {
                if (checkUserInputPasswordAndNickname(getNickname(), getPassword()) && authenticate(getNickname(), getPassword())) {
                    JOptionPane.showMessageDialog(Registration.this, "Вы успешно зарегистрировались", "Регистрация", JOptionPane.INFORMATION_MESSAGE);
                    succeeded = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(Registration.this, "Логин и/или пароль невалиден", "Регистрация", JOptionPane.ERROR_MESSAGE);
                    textFieldNickname.setText("");
                    passwordField.setText("");
                    succeeded = false;
                }
            } catch (SQLException sqlException) {
                JOptionPane.showMessageDialog(Registration.this, sqlException.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buttonRegistration);
        buttonPanel.add(buttonCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public boolean authenticate(String getNickname, String password) throws SQLException {
        return SQLService.registration(getNickname, password);
    }

    public String getNickname() {
        return textFieldNickname.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    private boolean checkUserInputPasswordAndNickname(String nickname, String password) {
        return Validator.isValidNickname(nickname) && Validator.isValidPassword(password);
    }
}
