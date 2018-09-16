package yel1ow.messenger.client.GUI;

import yel1ow.messenger.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PortInputWindow extends JFrame implements ActionListener {

    private final String CONNECTION_ERROR = "Connection error";
    private final String INVALID_FORMAT = "Invalid format";
    public static final String SPACE = " ";
    private static final int WIDTH = 320;
    private static final int HEIGHT = 240;
    private final Client client;

    private final JTextField portInputTextField = new JTextField(6);
    private final JLabel errorOutputLabel = new JLabel(SPACE);
    JPanel middlePanel = new JPanel();

    public PortInputWindow(Client client) {
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
                GridBagConstraints.CENTER, GridBagConstraints.CENTER,
                new Insets(0, 0, 0, 0), 0, 0));

        portInputTextField.addActionListener(this);
        middlePanel.add(new JLabel("Port: "));
        middlePanel.add(portInputTextField);
        add(middlePanel, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.CENTER,
                new Insets(0, 0, 0, 0), 0, 0));

        add(new JLabel(SPACE), new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.CENTER,
                new Insets(0, 0, 0, 0), 0, 0));

        add(new JLabel(SPACE), new GridBagConstraints(0, 2, 3,1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.CENTER,
                new Insets(0, 0, 0, 0), 0, 0));

        setAlwaysOnTop(true);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = portInputTextField.getText();
        if(msg.equals("")) {
            return;
        }
        portInputTextField.setText(null);
        try {
            if(!client.onPortInput(Integer.parseInt(msg))) {
                printError(CONNECTION_ERROR);
                return;
            }
        } catch (NumberFormatException NFE) {
            printError(INVALID_FORMAT);
            return;
        }
        dispose();
    }

    public void printError(String error) {
        errorOutputLabel.setText(error);
        middlePanel.setBackground(Color.RED);
    }
}
