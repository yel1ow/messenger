package yel1ow.messenger.client.GUI;

import yel1ow.messenger.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthorizationWindow extends JFrame {

    private final String LOG_IN = "Log in";
    private final String REGISTER_NOW = "Register now";
    public static final String SPACE = " ";
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;
    private final Client client;

    private final JTextField loginInputTextField = new JTextField(10);
    private final JTextField passwordInputTextField = new JPasswordField(10);
    private final JLabel errorOutputLabel = new JLabel(SPACE);
    private final JPanel middlePanel = new JPanel(new GridBagLayout());
    private final JButton loginButton = new JButton(LOG_IN);
    private final JButton registrationButton = new JButton(REGISTER_NOW);

    public AuthorizationWindow(Client client) {
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

        middlePanel.add(new JLabel("login: "), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.CENTER,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(loginInputTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(new JLabel(SPACE), new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(new JLabel("password: "), new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.CENTER,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(passwordInputTextField, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(new JLabel(SPACE), new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLogInButtonPress();
            }
        });
        middlePanel.add(loginButton, new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        middlePanel.add(new JLabel(SPACE), new GridBagConstraints(0, 5, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        registrationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRegistrationButtonPress();
            }
        });
        middlePanel.add(registrationButton, new GridBagConstraints(0, 6, 2, 1, 1.0, 1.0,
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
                loginInputTextField.setBackground(Color.WHITE);
                passwordInputTextField.setBackground(Color.WHITE);
            }
        });
    }

    private void onLogInButtonPress() {
        returnDefaultState();//Default background colors
        String login = loginInputTextField.getText();
        if(login.equals("")) {
            return;
        }
        String password = passwordInputTextField.getText();
        if(password.equals("")) {
            return;
        }
        if(login.contains(SPACE)) {
            printError("Error! Login contains spaces");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    loginInputTextField.setBackground(Color.ORANGE);
                }
            });
            return;
        }
        if(password.contains(SPACE)) {
            printError("Error! Password contains spaces");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    passwordInputTextField.setBackground(Color.ORANGE);
                }
            });
            return;
        }
        //loginInputTextField.setText(null);
        //passwordInputTextField.setText(null);
        client.logInButtonPressed(login, password);
    }

    private void onRegistrationButtonPress() {
        client.registrationWindow = new RegistrationWindow(client);
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
