package yel1ow.messenger.client.GUI;

import yel1ow.messenger.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class RegistrationWindow extends JFrame {
    private final String LOG_IN = "Log in";
    private final String REGISTER_NOW = "Register now";
    public static final String SPACE = " ";
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;
    private final Client client;

    private final JTextField nameInputTextField = new JTextField(10);
    private final JTextField surnameInputTextField = new JTextField(10);
    private final JTextField loginInputTextField = new JTextField(10);
    private final JTextField passwordInputTextField = new JPasswordField(10);
    private final JTextField passwordConfirmInputTextField = new JPasswordField(10);
    private final JLabel errorOutputLabel = new JLabel(SPACE);
    private final JPanel middlePanel = new JPanel(new GridBagLayout());
    private final JButton loginButton = new JButton(LOG_IN);
    private final JButton registrationButton = new JButton(REGISTER_NOW);

    public RegistrationWindow(Client client) {
        super(Client.NAME);
        this.client = client;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());

        add(errorOutputLabel, new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.CENTER,
                new Insets(0, 0, 0, 0), 0, 0));

        add(new JLabel(SPACE), new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(new JLabel("name: "), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.CENTER,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(nameInputTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(new JLabel(SPACE), new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(new JLabel("surname: "), new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.CENTER,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(surnameInputTextField, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(new JLabel(SPACE), new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(new JLabel("login: "), new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.CENTER,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(loginInputTextField, new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(new JLabel(SPACE), new GridBagConstraints(0, 5, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(new JLabel("password: "), new GridBagConstraints(0, 6, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.CENTER,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(passwordInputTextField, new GridBagConstraints(1, 6, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(new JLabel(SPACE), new GridBagConstraints(0, 7, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(new JLabel("confirm password: "), new GridBagConstraints(0, 8, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.CENTER,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(passwordConfirmInputTextField, new GridBagConstraints(1, 8, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(new JLabel(SPACE), new GridBagConstraints(0, 9, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        registrationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRegistrationButtonPress();
            }
        });
        middlePanel.add(registrationButton, new GridBagConstraints(0, 10, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(new JLabel(SPACE), new GridBagConstraints(0, 11, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLogInButtonPress();
            }
        });
        middlePanel.add(loginButton, new GridBagConstraints(0, 12, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        add(middlePanel, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        add(new JLabel(), new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        add(new JLabel(), new GridBagConstraints(0, 2, 3,1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        setAlwaysOnTop(true);
        setVisible(true);
    }

    private void returnDefaultState() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //printError("");
                nameInputTextField.setBackground(Color.WHITE);
                surnameInputTextField.setBackground(Color.WHITE);
                loginInputTextField.setBackground(Color.WHITE);
                passwordInputTextField.setBackground(Color.WHITE);
                passwordConfirmInputTextField.setBackground(Color.WHITE);
            }
        });
    }

    private boolean containsLettersOnly(String string) {
        for(int i = 0; i < string.length(); i++) {
            if(!Character.isLetter(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    boolean textFieldIsEmpty(JTextField textField) {
        if(textField.getText().equals("")) {
            textField.setBackground(Color.ORANGE);
            return true;
        }
        return false;
    }

    private void onRegistrationButtonPress() {

        returnDefaultState();

//        if(textFieldIsEmpty(nameInputTextField) || textFieldIsEmpty(surnameInputTextField) || textFieldIsEmpty(loginInputTextField) ||
//                textFieldIsEmpty(passwordInputTextField) || textFieldIsEmpty(passwordConfirmInputTextField)) {
//            printError("Fill all fields");
//            return;
//        }

        String surname = surnameInputTextField.getText();
        if(!containsLettersOnly(surname)) {
            printError("Error! Surname contains forbidden symbols");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    surnameInputTextField.setBackground(Color.ORANGE);
                }
            });
            return;
        }

        String name = nameInputTextField.getText();
        if(!containsLettersOnly(name)) {
            printError("Error! Name contains forbidden symbols");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    nameInputTextField.setBackground(Color.ORANGE);
                }
            });
            return;
        }

        String login = loginInputTextField.getText();
        if(login.contains(SPACE)) {
            printError("Error! Login contains spaces ");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    loginInputTextField.setBackground(Color.ORANGE);
                }
            });
            return;
        }

        String password = passwordInputTextField.getText();
        if(password.contains(SPACE)) {
            printError("Error! Password contains spaces ");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    passwordInputTextField.setBackground(Color.ORANGE);
                }
            });
            return;
        }

        String passwordConfirm = passwordConfirmInputTextField.getText();
        if(passwordConfirm.contains(SPACE)) {
            printError("Error! Password confirm contains spaces");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    passwordConfirmInputTextField.setBackground(Color.ORANGE);
                }
            });
            return;
        }

        if(!password.equals(passwordConfirm)) {
            printError("Passwords do not match");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    passwordConfirmInputTextField.setBackground(Color.ORANGE);
                }
            });
            return;
        }

        client.printMsg(name + surname + login + password + passwordConfirm);

        ArrayList<String> fields = new ArrayList<>();
        fields.add(name);
        fields.add(surname);
        fields.add(login);
        fields.add(password);

        client.registrationButtonPressed(fields);
    }

    private void onLogInButtonPress() {
        client.authorizationWindow = new AuthorizationWindow(client);
        dispose();
    }

    public void printError(String error) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                errorOutputLabel.setText(error);
            }
        });
    }
}