package yel1ow.messenger.client.GUI;

import yel1ow.messenger.client.Client;
import yel1ow.messenger.client.GUI.table_models.DefaultTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame {
    public static final String SPACE = " ";
    private static final int WIDTH = 900;
    private static final int HEIGHT = 550;
    private final Client client;

//    private final DefaultListModel<String> messagesListModel = new DefaultListModel<>();
//    private final JList<String> messagesList = new JList<>(messagesListModel);
//    final JScrollPane messagesScrollPane = new JScrollPane(messagesList);
    private final DefaultTableModel messagesTableModel = new DefaultTableModel();
    private final JTable messagesTable = new JTable(messagesTableModel);
    public final DefaultTableModel usersTableModel = new DefaultTableModel();
    private final JTable usersTable = new JTable(usersTableModel);

    public static void main(String[] args) {
        new MainWindow(null);
    }

    public MainWindow(Client client) {
        super(Client.NAME);
        this.client = client;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());


//        messagesList.setLayoutOrientation(JList.VERTICAL);
//        messagesList.setPreferredSize(new Dimension(200, 1000));

        messagesTable.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                messagesTable.scrollRectToVisible(messagesTable.getCellRect(messagesTable.getRowCount()-1, 0, true));
            }
        });
        messagesTable.setShowGrid(false);
        messagesTable.setBackground(Color.WHITE);

        final JScrollPane messagesTableScrollPane = new JScrollPane(messagesTable);
        //messagesTableScrollPane.setPreferredSize(new Dimension(250, 200));

        final JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(250, 200));
        leftPanel.add(messagesTableScrollPane, BorderLayout.CENTER);

        final JTextField inputMessagesTextField = new JTextField(25);
        inputMessagesTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onMessageEntered(inputMessagesTextField.getText());
                inputMessagesTextField.setText(null);
            }
        });
        leftPanel.add(inputMessagesTextField, BorderLayout.SOUTH);
        leftPanel.add(new JLabel("Good morning!!!!!"), BorderLayout.NORTH);


        usersTable.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                usersTable.scrollRectToVisible(usersTable.getCellRect(usersTable.getRowCount() - 1, 0, true));
            }
        });
        usersTable.setShowGrid(false);
        usersTable.setBackground(Color.WHITE);

        final JScrollPane usersTableScrollPane = new JScrollPane(usersTable);
        //usersTableScrollPane.setPreferredSize(new Dimension(10, 100));

        final JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(50, 200));
        rightPanel.add(new JTextField(), BorderLayout.SOUTH);
        rightPanel.add(usersTableScrollPane, BorderLayout.CENTER);
        rightPanel.add(new JLabel("Users online"), BorderLayout.NORTH);

        leftPanel.setBackground(Color.ORANGE);
        rightPanel.setBackground(Color.YELLOW);

        add(leftPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        add(rightPanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

//        add(new JLabel("izdatel'stvo yellow (https://github.com/yel1ow...)"), new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0,
//                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
//                new Insets(0, 0, 0, 0), 0, 0));

        //pack();

        setVisible(true);

        printMessage("Hello");
    }

    private void onMessageEntered(String message) {
        if(message == null) {
            return;
        }
        if(message.length() > 60) {
            JOptionPane.showMessageDialog(null, "Ты чё, краЁв не видишь?");
        }
        if(!message.equals(""))
        client.messageEntered(message);
    }

    public void printMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            messagesTableModel.addData(message + "\n");
            messagesTableModel.fireTableDataChanged();
            //JTable:
            //messagesTable.scrollRectToVisible();
            //messagesTable.changeSelection(messagesTable.getRowCount() - 1, 0, false, false);
            //messagesTable.scrollRectToVisible(messagesTable.getCellRect(messagesTableModel.getRowCount() - 1, messagesTableModel.getColumnCount(), true));
            //JList:
            //messagesListModel.addElement(message + "\n");
            //messagesList.ensureIndexIsVisible(messagesListModel.getSize() - 1);
        });
    }
}