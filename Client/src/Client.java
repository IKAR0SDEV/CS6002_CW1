import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

/**
 * @author khali
 */
public class Client extends JFrame implements ActionListener, Runnable {

    private BufferedWriter writer;
    private BufferedReader reader;
    private JButton sendBtn;
    public static JTextArea messageArea;
    private JTextField messageInput;
    public static JTextField usernameField;

    public Client() {
        initComponents();
        try {
            // Create a socket to connect to the server
            Socket socketClient = new Socket("localhost", 2003);
            // Create an output stream to send data to the server
            writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
            // Create an input stream to get data from the server
            reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public static void main(String[] args) {
        //Run Client GUI
        Client client = new Client();
        client.setLocationRelativeTo(null);
        client.setVisible(true);
        client.setTitle("Messenger");
        client.setResizable(false);
        //Declare thread
        Thread thread = new Thread(client);
        //Start Client Thread
        thread.start();
    }

    //GUI Components Initialization
    private void initComponents() {
        JScrollPane jScrollPane1 = new JScrollPane();
        messageArea = new JTextArea();
        messageInput = new JTextField();
        sendBtn = new JButton();
        JButton sendFileBtn = new JButton();
        JLabel jLabel1 = new JLabel();
        usernameField = new JTextField();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        messageArea.setEditable(false);
        messageArea.setColumns(20);
        messageArea.setFont(new java.awt.Font("f", Font.PLAIN, 18)); // NOI18N
        messageArea.setRows(5);
        messageArea.setEnabled(true);
        jScrollPane1.setViewportView(messageArea);
        DefaultCaret caret = (DefaultCaret) messageArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        messageInput.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 15)); // NOI18N
        messageInput.setToolTipText("");
        //Add key listener to the message input field, which will send the message when enter key is pressed
        messageInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendBtn.doClick();
                }
            }

        });

        sendBtn.setText("Send");
        sendBtn.addActionListener(this);

        sendFileBtn.setText("Send File");
        sendFileBtn.addActionListener(this::sendFileBtnActionPerformed);

        jLabel1.setText("Enter Username: ");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(messageInput)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(sendFileBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(sendBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(usernameField, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(usernameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 186, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(messageInput)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(sendBtn)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                                                .addComponent(sendFileBtn)))
                                .addContainerGap())
        );

        pack();
    }

    //Launch Send file window on button click
    private void sendFileBtnActionPerformed(java.awt.event.ActionEvent evt) {
        Send send = new Send();
        send.setLocationRelativeTo(null);
        send.setVisible(true);
        send.setTitle("Send File");
        send.setResizable(false);
        send.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    //Send message to server
    public void actionPerformed(ActionEvent ae) {
        String username, msg;
        //Get username and message from text fields, if empty show error message
        if (usernameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter your Username", "Enter Username!", JOptionPane.ERROR_MESSAGE);
            return;
        } else username = usernameField.getText();
        //Get message from text field, if empty show error message
        if (messageInput.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter the message", "Enter Message!", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            msg = messageInput.getText();
        }
        //Send message to server
        String str = ">>>" + username + ": \n" + msg;
        try {
            writer.write(str);
            writer.write("\r\n");
            writer.flush();
        } catch (Exception e) {
            messageArea.append("\nMessage could not be sent.\nServer is not responding.\n");
        }
        //Clear text field
        messageInput.setText("");

    }

    public void run() {
        try {
            String msg;
            //Read message from server, if not empty show message in text area
            while ((msg = reader.readLine()) != null) {
                messageArea.append(msg + "\n");
            }
        } catch (Exception e) {
            messageArea.append("Connection Closed\nRestart the server to continue.\n");
        }
    }

}